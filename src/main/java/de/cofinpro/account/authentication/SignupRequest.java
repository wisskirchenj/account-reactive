package de.cofinpro.account.authentication;

import javax.validation.constraints.NotEmpty;

public record SignupRequest(@NotEmpty String name, @NotEmpty String lastname, @NotEmpty String email,
                            @NotEmpty String password) {
}
