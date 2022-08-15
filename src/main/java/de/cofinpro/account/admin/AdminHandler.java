package de.cofinpro.account.admin;

import de.cofinpro.account.authentication.SignupResponse;
import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import de.cofinpro.account.persistence.LoginRoleReactiveRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        return ok().bodyValue("not implemented!");
    }

    public Mono<ServerResponse> toggleRole(ServerRequest serverRequest) {
        return ok().bodyValue("not implemented!");
    }
}
