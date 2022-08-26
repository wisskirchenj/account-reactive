package de.cofinpro.account.audit;

import java.time.LocalDate;

/**
 * immutable AuditEventResponse for the AUDITOR accessed endpoint /api/security/events returning a list of these.
 */
public record AuditEventResponse(long id, LocalDate date, String action,
                                 String subject, String object, String path) {
}
