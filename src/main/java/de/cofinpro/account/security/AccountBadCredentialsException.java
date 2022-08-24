package de.cofinpro.account.security;

import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;

@Getter
public class AccountBadCredentialsException extends BadCredentialsException {

    private final String user;

    public AccountBadCredentialsException(String message, String user) {
        super(message);
        this.user = user;
    }
}
