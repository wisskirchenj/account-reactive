package de.cofinpro.account.persistence;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive Crud-Repository for the Login entities.
 */
@Repository
public interface LoginReactiveRepository extends ReactiveSortingRepository<Login, Long> {

    Mono<Login> findByEmail(String email);

    Mono<Void> deleteByEmail(String email);

    default Mono<Login> toggleLock(String email, boolean lockRequested) {
        return findByEmail(email)
                .map(login -> login.setAccountLocked(lockRequested))
                .flatMap(this::save);
    }
}
