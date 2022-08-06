package de.cofinpro.account.domain;

/**
 * response object to the /api/empl/payment GET endpoint.
 */
public record EmployeeResponse(long id, String name, String lastname, String email) {
}
