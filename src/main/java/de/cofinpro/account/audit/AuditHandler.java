package de.cofinpro.account.audit;

import de.cofinpro.account.persistence.SecurityEvent;
import de.cofinpro.account.persistence.SecurityEventReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * service layer handler class for all audit specific endpoints: /api/security/events (GET).
 */
@Service
@Slf4j
public class AuditHandler {

    private final SecurityEventReactiveRepository auditRepository;

    public AuditHandler(SecurityEventReactiveRepository securityEventReactiveRepository) {
        this.auditRepository = securityEventReactiveRepository;
    }

    /**
     * controller handler for GET endpoint /api/security/events available only to authorized users with AUDITOR role. It
     * provides the auditor with a list of all (persistent) security events ordered by id.
     * @return ServerResponse Mono with a list of all security events from application runs stored in the database.
     */
    public Mono<ServerResponse> getAuditEvents(ServerRequest ignoredRequest) {
        log.info("get request for audit events received");
        return ok().body(auditRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .map(SecurityEvent::toResponse), AuditEventResponse.class);
    }
}
