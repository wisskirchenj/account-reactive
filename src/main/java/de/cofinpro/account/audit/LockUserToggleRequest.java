package de.cofinpro.account.audit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static de.cofinpro.account.configuration.AuthenticationConfiguration.EMAIL_REGEX;

public record LockUserToggleRequest(@NotNull
                                    @Pattern(regexp = EMAIL_REGEX, message = "Not a valid corporate Email")
                                    String user,
                                    @NotNull
                                    @Pattern(regexp = "(?i)(un)*lock", message = "operation must be 'lock' or 'unlock'")
                                    String operation) {
}
