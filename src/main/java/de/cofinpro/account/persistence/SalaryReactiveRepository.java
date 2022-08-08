package de.cofinpro.account.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SalaryReactiveRepository extends ReactiveCrudRepository<Salary, Long> {

    @Query("SELECT * FROM SALARY WHERE EMAIL = $1 AND PERIOD = $2")
    Mono<Salary> findByEmployeeAndPeriod(String email, String period);
}
