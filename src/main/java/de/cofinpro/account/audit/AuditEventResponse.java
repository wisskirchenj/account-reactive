package de.cofinpro.account.audit;

import java.time.LocalDate;

public record AuditEventResponse(long id, LocalDate date, String action,
                                 String subject, String object, String path) {
}
