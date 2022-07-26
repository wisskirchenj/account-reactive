package de.cofinpro.account.authentication;

import java.util.List;

/**
 * Json response after successful addition of a user via signup http
 * @param id database id
 */
public record SignupResponse(long id, String name, String lastname, String email, List<String> roles) {
}
