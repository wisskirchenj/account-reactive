package de.cofinpro.account.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive Crud-Repository for the Login entities.
 */
@Repository
public interface LoginReactiveRepository extends ReactiveCrudRepository<Login, Long> {

    Mono<UserDetails> findByEmail(String email);
}
