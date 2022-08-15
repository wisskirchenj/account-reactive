package de.cofinpro.account.admin;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
public class AdminHandler {
    public Mono<ServerResponse> displayUsers(ServerRequest serverRequest) {
        return ok().bodyValue("not implemented!");
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        return ok().bodyValue("not implemented!");
    }

    public Mono<ServerResponse> toggleRole(ServerRequest serverRequest) {
        return ok().bodyValue("not implemented!");
    }
}
