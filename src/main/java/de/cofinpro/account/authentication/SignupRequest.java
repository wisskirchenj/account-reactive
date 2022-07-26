package de.cofinpro.account.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class SignupRequest {

    @NotEmpty
    private final String name;
    @NotEmpty
    private final String lastname;
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;

    public SignupResponse toResponse() {
        return new SignupResponse(name, lastname, email);
    }
}
