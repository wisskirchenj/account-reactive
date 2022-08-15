package de.cofinpro.account.admin;

import de.cofinpro.account.authentication.SignupResponse;
import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import de.cofinpro.account.persistence.LoginRoleReactiveRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;

import static de.cofinpro.account.configuration.AdminConfiguration.*;
import static de.cofinpro.account.configuration.AuthenticationConfiguration.EMAIL_REGEX;
import static java.lang.Boolean.TRUE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
public class AdminHandler {

    private final LoginReactiveRepository userRepository;
    private final LoginRoleReactiveRepository roleRepository;
    private final Validator validator;

    public AdminHandler(LoginReactiveRepository userRepository,
                        LoginRoleReactiveRepository roleRepository, Validator validator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
                                .then(userRepository.deleteByEmail(email))
                                .then(Mono.just(new UserDeletedResponse(email, DELETED_SUCCESSFULLY)));
                    }
                });
    }

    private Mono<Boolean> isAdmin(List<String> roles) {
        if (roles.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_ERRORMSG));
        }
        return Mono.just(roles.contains("ROLE_ADMINISTRATOR"));
    }

    public Mono<ServerResponse> toggleRole(ServerRequest serverRequest) {
        return ok().bodyValue("not implemented!");
    }
}
