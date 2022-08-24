package de.cofinpro.account.security;

import de.cofinpro.account.audit.BruteForceProtector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static de.cofinpro.account.configuration.AuthenticationConfiguration.PASSWORD_HACKED_ERRORMSG;
import static de.cofinpro.account.configuration.AuthenticationConfiguration.passwordIsHacked;

/**
 * custom ReactiveAuthenticationManager, that makes sure, that no user can login with a password, that
 * is known as been hacked - even if it matches the stored password to such a user (which may have been saved before
 * the hacking took place.
 */
@Component
public class AccountReactiveAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    private final BruteForceProtector bruteForceProtector;

    @Autowired
    public AccountReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                PasswordEncoder passwordEncoder, BruteForceProtector bruteForceProtector) {
        super(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.bruteForceProtector = bruteForceProtector;
    }

    /**
     * overridden authenticate method, that pre-applies the check if the given password was hacked.
     * @param authentication the {@link Authentication} to test
     * @return error Mono requesting the user to change password if check fails, default return of super.authenticate() else.
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (passwordIsHacked((String) authentication.getCredentials())) {
            return Mono.error(new AccountBadCredentialsException(PASSWORD_HACKED_ERRORMSG + " Please change!",
                    authentication.getName()));
        }
        return super.authenticate(authentication)
                .doOnSuccess(auth -> bruteForceProtector.resetLoginFailures(auth.getName()))
                .onErrorMap(exception -> new AccountBadCredentialsException(exception.getMessage(),
                        authentication.getName()));
    }
}