package de.cofinpro.account.domain;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * service layer handler class for all domain (account) specific endpoints
 */
@Service
public class AccountHandler {

    private final LoginReactiveRepository userRepository;

    public AccountHandler(LoginReactiveRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * controller handler for GET endpoint available only to authenticated users /api/empl/payment, that gives access to
     * the payment data for the authenticated user. -> in stage 2 (tempor.) returns only the user data...
     * @param request the ServerRequest containing the user's principal data.
     * @return an EmployeeResponse object
     */
    public Mono<ServerResponse> accessPayrolls(ServerRequest request) {
        return request.principal()
                .flatMap(principal -> ServerResponse.ok()
                        .body(userRepository.findByEmail(principal.getName()).map(Login::createEmployeeResponse),
                                EmployeeResponse.class));
    }
}
