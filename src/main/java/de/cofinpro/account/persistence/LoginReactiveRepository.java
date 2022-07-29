package de.cofinpro.account.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface LoginReactiveRepository extends ReactiveCrudRepository<Login, Long> {

    @Query("SELECT * FROM LOGIN WHERE EMAIL = $1")
    Mono<UserDetails> findByEmail(String email);
}
