package de.cofinpro.account.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive Crud-Repository for the Login entities.
 */
@Repository
public interface LoginReactiveRepository extends ReactiveSortingRepository<Login, Long>,
        ReactiveCrudRepository<Login, Long> {

    Mono<Login> findByEmailIgnoreCase(String email);

    Mono<Void> deleteByEmailIgnoreCase(String email);

    /**
     * lock or unlock a user n the system and reset the failed logins to 0.
     * @param email         user's email as key
     * @param lockRequested true means lock the user, false unlock
     * @return the save result
     */
    default Mono<Login> toggleLock(String email, boolean lockRequested) {
        return findByEmailIgnoreCase(email)
                .map(login -> login.setAccountLocked(lockRequested).setFailedLogins(0))
                .flatMap(this::save);
    }
}
