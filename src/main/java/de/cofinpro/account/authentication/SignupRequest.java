package de.cofinpro.account.authentication;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * immutable signup request record received via http post
 * @param name not empty
 * @param lastname not empty
 * @param email only corporate email ending with "@acme.com"
 * @param password not empty
 */
public record SignupRequest(@NotEmpty String name, @NotEmpty String lastname,
                            @NotEmpty @Pattern(regexp ="\\w+(\\.\\w+){0,2}@acme.com") String email,
                            @NotEmpty String password) {
}
