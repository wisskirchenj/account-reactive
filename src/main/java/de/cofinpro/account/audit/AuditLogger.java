package de.cofinpro.account.audit;

import de.cofinpro.account.admin.RoleToggleRequest;
import de.cofinpro.account.persistence.SecurityEvent;
import de.cofinpro.account.persistence.SecurityEventReactiveRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
public class AuditLogger {

    private final SecurityEventReactiveRepository auditRepository;

    public AuditLogger(SecurityEventReactiveRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public Mono<SecurityEvent> logCreateUser(String newUser) {
        return auditRepository.save(SecurityEvent.builder().date(LocalDate.now()).action("CREATE_USER")
                .subject("Anonymous").object(newUser).path("/api/auth/signup").build());
    }

    public Mono<SecurityEvent> logToggleRole(String admin, RoleToggleRequest roleToggleRequest) {
        String action = roleToggleRequest.operation().toUpperCase() + "_ROLE";
        String role = roleToggleRequest.role().toUpperCase();
        String object = roleToggleRequest.operation().equalsIgnoreCase("GRANT")
                ? "Grant role %s to %s".formatted(role, roleToggleRequest.user())
                : "Remove role %s from %s".formatted(role, roleToggleRequest.user());
        return auditRepository.save(SecurityEvent.builder().date(LocalDate.now()).action(action)
                .subject(admin).object(object).path("/api/admin/user/role").build());
    }
}
