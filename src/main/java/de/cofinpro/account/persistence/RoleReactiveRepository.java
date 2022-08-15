package de.cofinpro.account.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Reactive CRUD-Repository for the Role entities.
 */
@Repository
public interface RoleReactiveRepository extends ReactiveCrudRepository<Role, Long> {
}
