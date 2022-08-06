package de.cofinpro.account.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public record ChangepassRequest(@NotEmpty @JsonProperty("new_password") String newPassword) {
}
