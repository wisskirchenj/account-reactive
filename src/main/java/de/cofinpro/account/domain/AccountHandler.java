package de.cofinpro.account.domain;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class AccountHandler {

    private final LoginReactiveRepository userRepository;

    public AccountHandler(LoginReactiveRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> accessPayrolls(ServerRequest request) {
        return request.principal()
                .flatMap(principal -> ServerResponse.ok()
                        .body(userRepository.findByEmail(principal.getName()).map(Login::toResponse),
                                EmployeeResponse.class));
    }
}
