package de.cofinpro.account.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SignupResponse {

    private final String name;
    private final String lastname;
    private final String email;
}
