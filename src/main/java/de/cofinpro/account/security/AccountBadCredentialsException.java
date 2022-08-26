package de.cofinpro.account.security;

import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * custom bad credentials exception that also transports the affected user taken from the authentication for
 * security logging
 */
@Getter
public class AccountBadCredentialsException extends BadCredentialsException {

    private final String user;

    public AccountBadCredentialsException(String message, String user) {
        super(message);
        this.user = user;
    }
}
