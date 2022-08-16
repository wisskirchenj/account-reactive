package de.cofinpro.account.admin;

import de.cofinpro.account.authentication.SignupResponse;
import de.cofinpro.account.persistence.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static de.cofinpro.account.configuration.AdminConfiguration.*;
import static de.cofinpro.account.configuration.AuthenticationConfiguration.EMAIL_REGEX;
import static java.lang.Boolean.TRUE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
public class AdminHandler {

    private final LoginReactiveRepository userRepository;
    private final LoginRoleReactiveRepository roleRepository;
    private final SalaryReactiveRepository salaryRepository;
    private final List<Role> systemRoles;
    private final Validator validator;

    public AdminHandler(LoginReactiveRepository userRepository,
                        LoginRoleReactiveRepository roleRepository,
                        SalaryReactiveRepository salaryRepository,
                        List<Role> systemRoles,
                        Validator validator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.salaryRepository = salaryRepository;
        this.systemRoles = systemRoles;
        this.validator = validator;
    }

    public Mono<ServerResponse> displayUsers(ServerRequest ignoredServerRequest) {
        return ok().body(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                        .flatMap(login -> Mono.just(login)
                                .zipWith(roleRepository.findRolesByEmail(login.getEmail()), Login::setRoles))
                        .map(Login::toSignupResponse), SignupResponse.class);
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String email = request.pathVariable("email");
        if (!email.matches(EMAIL_REGEX)) {
            return Mono.error(new ServerWebInputException("Invalid user email given: '" + email + "'!"));
        }
        return ok().body(deleteUser(email), UserDeletedResponse.class);
    }

    private Mono<UserDeletedResponse> deleteUser(String email) {
        return roleRepository.findRolesByEmail(email)
                .flatMap(this::isAdmin)
                .flatMap(isAdmin -> {
                    if (TRUE.equals(isAdmin)) {
                        return Mono.error(new ServerWebInputException(CANT_DELETE_ADMIN_ERRORMSG));
                    } else {
                        return roleRepository.deleteAllByEmail(email)
                                .then(salaryRepository.deleteAllByEmail(email))
                                .then(userRepository.deleteByEmail(email))
                                .then(Mono.just(new UserDeletedResponse(email, DELETED_SUCCESSFULLY)));
                    }
                });
    }

    private Mono<Boolean> isAdmin(List<String> roles) {
        if (roles.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_ERRORMSG));
        }
        return Mono.just(roles.contains(ADMIN_ROLE));
    }

    public Mono<ServerResponse> toggleRole(ServerRequest request) {
        return request.bodyToMono(RoleToggleRequest.class)
                        .flatMap(req -> ok().body(validateAndToggleRole(req), SignupResponse.class));
    }

    private Mono<SignupResponse> validateAndToggleRole(RoleToggleRequest roleToggleRequest) {
        String hibernateValidationErrors = validateHibernate(roleToggleRequest);
        if (!hibernateValidationErrors.isEmpty()) {
            return Mono.error(new ServerWebInputException(hibernateValidationErrors));
        }
        if (systemRoles.stream().map(Role::getRoleName)
                .noneMatch(role -> role.endsWith(roleToggleRequest.role().toUpperCase()))) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, ROLE_NOT_FOUND_ERRORMSG));
        }
        return roleRepository
                .findRolesByEmail(roleToggleRequest.user())
                .flatMap(userRoles -> isOperationValid(userRoles, roleToggleRequest))
                .flatMap(requestedRole -> roleToggleRequest.operation().equalsIgnoreCase("remove")
                        ? roleRepository.deleteByEmailAndRole(roleToggleRequest.user(), requestedRole)
                            .then(updatedUserResponse(roleToggleRequest.user()))
                        : roleRepository.save(LoginRole.builder().email(roleToggleRequest.user()).role(requestedRole).build())
                            .then(updatedUserResponse(roleToggleRequest.user()))
                );
    }

    private Mono<SignupResponse> updatedUserResponse(String email) {
        return userRepository.findByEmail(email)
                .flatMap(login -> Mono.just(login)
                        .zipWith(roleRepository.findRolesByEmail(login.getEmail()), Login::setRoles))
                .map(Login::toSignupResponse);
    }

    private Mono<String> isOperationValid(List<String> userRoles, RoleToggleRequest roleToggleRequest) {
        if (userRoles.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_ERRORMSG));
        }

        boolean isRemove = roleToggleRequest.operation().equalsIgnoreCase("remove");
        String requestedRole = "ROLE_" + roleToggleRequest.role().toUpperCase();
        if (isRemove && !userRoles.contains(requestedRole)) {
            return Mono.error(new ServerWebInputException(USER_HASNT_ROLE_ERRORMSG));
        }
        if (isRemove && userRoles.size() == 1) {
            return Mono.error(new ServerWebInputException(requestedRole.equals(ADMIN_ROLE)
                        ? CANT_DELETE_ADMIN_ERRORMSG : USER_NEEDS_ROLE_ERRORMSG));
        }
        if (!isRemove && userRoles.contains(requestedRole)) {
            return Mono.error(new ServerWebInputException(USER_HAS_ROLE_ALREADY_ERRORMSG));
        }
        if (!isRemove &&
                (requestedRole.equals(ADMIN_ROLE) || userRoles.contains(ADMIN_ROLE))) {
            return Mono.error(new ServerWebInputException(INVALID_ROLE_COMBINE_ERRORMSG));
        }
        return Mono.just(requestedRole);
    }

    private String validateHibernate(RoleToggleRequest roleToggleRequest) {
        Errors errors = new BeanPropertyBindingResult(roleToggleRequest, RoleToggleRequest.class.getName());
        validator.validate(roleToggleRequest, errors);
        return errors.hasErrors() ? errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" && "))
                : "";
    }
}
