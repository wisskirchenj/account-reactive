package de.cofinpro.account.admin;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static de.cofinpro.account.configuration.AuthenticationConfiguration.EMAIL_REGEX;

/**
 * Immutable request type for the PUT /api/admin/user/role endpoint with validation annotations
 */
public record RoleToggleRequest(@NotNull
                                @Pattern(regexp = EMAIL_REGEX, message = "Not a valid corporate Email")
                                String user,
                                @NotEmpty
                                String role,
                                @NotNull
                                @Pattern(regexp = "(?i)grant|remove", message = "operation needs 'grant' or 'remove'")
                                String operation) {
}
