package de.cofinpro.account.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Reactive CRUD-Repository for the LoginRole entities.
 */
@Repository
public interface LoginRoleReactiveRepository extends ReactiveCrudRepository<LoginRole, Long> {

    Flux<LoginRole> findAllByEmail(String email);

    Mono<Void> deleteAllByEmail(String email);

    Mono<Void> deleteByEmailAndRole(String email, String role);

    default Mono<List<String>> findRolesByEmail(String email) {
        return findAllByEmail(email).map(LoginRole::getRole).collectList();
    }
}
