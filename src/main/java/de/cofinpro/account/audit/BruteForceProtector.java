package de.cofinpro.account.audit;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import de.cofinpro.account.persistence.SecurityEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static de.cofinpro.account.configuration.AdminConfiguration.LOGIN_FAILED_LIMIT;

/**
 * bean class that handles the increment or reset of failed = unauthenticated logins. When the
 * LIMIT of failed logins is reached, it triggers the locking of the affected user.
 */
@Component
public class BruteForceProtector {

    private final AuditLogger auditLogger;
    private final LoginReactiveRepository userRepository;

    public BruteForceProtector(AuditLogger auditLogger, LoginReactiveRepository userRepository) {
        this.auditLogger = auditLogger;
        this.userRepository = userRepository;
    }

    /**
     * method that handles a failed login of a user given by email and the requested path where failure took place
     */
    public Mono<SecurityEvent> handleLoginFail(String email, String path) {
        return userRepository.findByEmail(email)
                .defaultIfEmpty(Login.unknown())
                .flatMap(login -> checkFailedAttemptsAndHandle(login, path));
    }


    /**
     * resets a users failed login attempts in the database
     */
    public void resetLoginFailures(String email) {
        userRepository.findByEmail(email)
                .flatMap(login -> userRepository.save(login.setFailedLogins(0)))
                .subscribe();
    }

    /**
     * an authentication failure of a user not in the system or a locked user is only logged here.
     * Otherwise, the users failed attempts is incremented and stored. If the limit is reached the lock action
     * on brut force is triggered.
     */
    private Mono<SecurityEvent> checkFailedAttemptsAndHandle(Login login, String path) {
        if (login.isUnknown() || login.isAccountLocked()) {
            return auditLogger.logFailedLogin(login.getEmail(), path);
        }
        login.setFailedLogins(login.getFailedLogins() + 1);
        if (login.getFailedLogins() < LOGIN_FAILED_LIMIT) {
            return userRepository.save(login).then(auditLogger.logFailedLogin(login.getEmail(), path));
        }
        return userRepository.toggleLock(login.getEmail(), true)
                .then(auditLogger.logFailedLogin(login.getEmail(), path))
                .then(auditLogger.logBruteForce(login.getEmail(), path));
    }
}