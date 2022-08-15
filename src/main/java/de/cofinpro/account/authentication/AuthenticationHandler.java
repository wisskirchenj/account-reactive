package de.cofinpro.account.authentication;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import de.cofinpro.account.persistence.LoginRole;
import de.cofinpro.account.persistence.LoginRoleReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import org.springframework.validation.Validator;
import reactor.util.function.Tuple2;

import java.security.Principal;
import java.util.List;

import static de.cofinpro.account.configuration.AuthenticationConfiguration.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * service layer class, that handles the authentication routes /api/auth/*, i.e. signup and changepass.
 */
@Service
@Slf4j
public class AuthenticationHandler {

    private final Validator validator;
    private final LoginReactiveRepository userRepository;
    private final LoginRoleReactiveRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationHandler(Validator validator,
                                 LoginReactiveRepository userRepository,
                                 LoginRoleReactiveRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * controller entry point (routing handler) for route /api/auth/signup.
     * request is validated and saved, if the user (email given) does not exist yet.
     * @param request The ServerRequest with a SignupRequest body.
     * @return a SignupResponse Json (200) as body of a ServerResponse or a 400 if validation error or user exists already
     */
    public Mono<ServerResponse> signup(ServerRequest request) {
        return request.bodyToMono(SignupRequest.class)
                .zipWith(userRepository.count())
                .flatMap(tuple -> ok().body(validateAndSave(tuple), SignupResponse.class));
    }

    /**
     * hibernate validate and save a singup request. If the count is 0, the signed up user gets the Administrator role,
     * otherwise a User role
     * @param tuple request data to validate and save - zipped with user-count
     * @return a signup response as Mono if new user saved, error Mono else.
     */
    private Mono<SignupResponse> validateAndSave(Tuple2<SignupRequest, Long> tuple) {
        SignupRequest signupRequest = tuple.getT1();
        String role = tuple.getT2() == 0 ? "ROLE_ADMINISTRATOR" : "ROLE_USER";
        Errors errors = new BeanPropertyBindingResult(signupRequest, SignupRequest.class.getName());
        validator.validate(signupRequest, errors);
        if (errors.hasErrors()) {
            return Mono.error(new ServerWebInputException(errors.getAllErrors().toString()));
        }
        String passwordValidationError = validatePassword(signupRequest.password());
        if (!passwordValidationError.isEmpty()) {
            return Mono.error(new ServerWebInputException(passwordValidationError));
        }
        return saveUser(signupRequest, role);
    }

    /**
     * validates, if password has minimum length dnd is not hacked (i.e. found in a list of breached passwords)
     * @param password the password to validate
     * @return empty String for valid password, informative error message else.
     */
    private String validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return PASSWORD_TOO_SHORT_ERRORMSG;
        }
        if (passwordIsHacked(password)) {
            return PASSWORD_HACKED_ERRORMSG;
        }
        return "";
    }

    /**
     * save method called after successful validation of the signup request.
     * checks if the given request email already exists as user in the database. If so, an error is returned (400)
     * or else the user is saved to the database
     * @param signupRequest already validated signup request data
     * @return a signup response mono on successful save, an error mono if user existed.
     */
    private Mono<SignupResponse> saveUser(SignupRequest signupRequest, String role) {
        return userRepository.findByEmail(signupRequest.email())
                .defaultIfEmpty(Login.unknown())
                .flatMap(user -> {
                    if (user.isUnknown()) {
                        return userRepository
                                .save(Login.fromSignupRequest(signupRequest,
                                        passwordEncoder.encode(signupRequest.password())))
                                .zipWith(roleRepository.save(LoginRole.builder().email(signupRequest.email()).role(role)
                                        .build()).map(LoginRole::getRole).map(List::of), Login::setRoles)
                                .map(Login::toSignupResponse);
                    } else {
                        return Mono.error(new ServerWebInputException(USER_EXISTS_ERRORMSG));
                    }});
    }

    /**
     * controller entry point (routing handler) for the authenticated route /api/auth/changepass.
     * request body containing new password is zipped with principal and validated for password update.
     * @param request The ServerRequest with a ChangepassRequest body.
     * @return a ChangepassResponse Json (200) as body of a ServerResponse or a 400 if validation error or same password
     */
    public Mono<ServerResponse> changePassword(ServerRequest request) {
        return request.bodyToMono(ChangepassRequest.class)
                .zipWith(request.principal())
                .flatMap(tuple -> ok().body(validateAndChangepass(tuple), ChangepassResponse.class));
    }

    /**
     * validates the password (length and not breached) and checks if it differs from last password.
     * If so, the Login-entity to this user is updated ad saved to the database.
     * @param tuple Tuple2 consisting of the ChangepassRequest and the user's principal
     * @return ChangepassResponse if password is updated or informative 400 error Mono
     */
    private Mono<ChangepassResponse> validateAndChangepass(Tuple2<ChangepassRequest, ? extends Principal> tuple) {
        final String newPassword = tuple.getT1().newPassword();
        String passwordValidationError = validatePassword(newPassword);
        if (!passwordValidationError.isEmpty()) {
            return Mono.error(new ServerWebInputException(passwordValidationError));
        }
        return userRepository.findByEmail(tuple.getT2().getName())
                .flatMap(user -> {
                    if (passwordEncoder.matches(newPassword, user.getPassword())) {
                        return Mono.error(new ServerWebInputException(SAME_PASSWORD_ERRORMSG));
                    } else {
                        user.setPassword(passwordEncoder.encode(newPassword));
                        return userRepository.save(user)
                                .map(login -> new ChangepassResponse(login.getEmail(), PASSWORD_UPDATEMSG));
                    }});
    }
}