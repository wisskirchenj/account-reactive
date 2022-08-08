package de.cofinpro.account.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive Crud-Repository for the Login entities.
 */
@Repository
public interface LoginReactiveRepository extends ReactiveCrudRepository<Login, Long> {

    @Query("SELECT * FROM LOGIN WHERE EMAIL = $1")
    Mono<UserDetails> findByEmail(String email);
}
