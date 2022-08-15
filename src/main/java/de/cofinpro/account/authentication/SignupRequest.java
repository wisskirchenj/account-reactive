package de.cofinpro.account.authentication;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static de.cofinpro.account.configuration.AuthenticationConfiguration.EMAIL_REGEX;

/**
 * immutable signup request record received via http post
 * @param name not empty
 * @param lastname not empty
 * @param email only corporate email ending with "@acme.com"
 * @param password not empty
 */
public record SignupRequest(@NotEmpty String name, @NotEmpty String lastname,
                            @NotNull @Pattern(regexp = EMAIL_REGEX) String email,
                            @NotEmpty String password) {
}
