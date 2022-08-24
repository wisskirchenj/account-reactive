package de.cofinpro.account.security;

import de.cofinpro.account.audit.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AccountServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final AuditLogger auditLogger;

    @Autowired
    public AccountServerAccessDeniedHandler(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return exchange.getPrincipal()
                .flatMap(user ->
                        auditLogger.logAccessDenied(user.getName(), exchange.getRequest().getPath().value()))
                .flatMap(secEvent ->
                        Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, denied.getMessage() + "!")));
    }
}
