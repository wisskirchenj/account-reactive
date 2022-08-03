package de.cofinpro.account.authentication;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
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

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * service layer class, that handles the authentication routes /api/auth/*, i.e. signup and changepass.
 */
@Service
@Slf4j
public class AuthenticationHandler {

    private final Validator validator;
    private final LoginReactiveRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationHandler(Validator validator,
                                 LoginReactiveRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.userRepository = userRepository;
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
                .flatMap(req -> ServerResponse.ok().body(validateAndSave(req), SignupResponse.class));
    }

    public Mono<ServerResponse> changePassword(ServerRequest ignoredServerRequest) {
        return ok().build();
    }

    /**
     * hibernate validated
     * @param signupRequest request data to validate and save
     * @return a signup response as Mono if new user saved, error Mono else.
     */
    private Mono<SignupResponse> validateAndSave(SignupRequest signupRequest) {
        Errors errors = new BeanPropertyBindingResult(signupRequest, SignupRequest.class.getName());
        validator.validate(signupRequest, errors);
        if (errors.hasErrors()) {
            return Mono.error(new ServerWebInputException(errors.getAllErrors().toString()));
        }
        return saveUser(signupRequest);
    }

    /**
     * save method called after successful validation of the signu request.
     * checks if the given request email already exists as user in the database. If so, an error is returned (400)
     * or else the user is saved to the data base
     * @param signupRequest already validated signup request data
     * @return a signup response mono on successful save, an error mono if user existed.
     */
    private Mono<SignupResponse> saveUser(SignupRequest signupRequest) {
        return userRepository.findByEmail(signupRequest.email())
                .defaultIfEmpty(Login.unknown())
                .flatMap(userDetails -> {
                    if (((Login) userDetails).isUnknown()) {
                        return userRepository
                                .save(Login.fromSignupRequest(signupRequest,
                                        passwordEncoder.encode(signupRequest.password())))
                                .map(Login::toSignupResponse);
                    } else {
                        return Mono.error(new ServerWebInputException("User exists!"));
                    }});
    }
}