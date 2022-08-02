package de.cofinpro.account.authentication;

/**
 * Json response after successful addition of a user via signup http
 * @param id database id
 * @param name
 * @param lastname
 * @param email
 */
public record SignupResponse(long id, String name, String lastname, String email) {
}
