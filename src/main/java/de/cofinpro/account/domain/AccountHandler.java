package de.cofinpro.account.domain;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import de.cofinpro.account.persistence.Salary;
import de.cofinpro.account.persistence.SalaryReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

import static de.cofinpro.account.configuration.AccountConfiguration.*;
import static java.lang.Boolean.TRUE;
import static java.util.function.Predicate.not;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * service layer handler class for all domain (account) specific endpoints /api/empl/payment (GET) and
 * /api/acct/payments (POST and PUT).
 */
@Service
@Slf4j
public class AccountHandler {

    private final LoginReactiveRepository userRepository;
    private final SalaryReactiveRepository salaryRepository;
    private final Validator validator;

    public AccountHandler(LoginReactiveRepository userRepository,
                          SalaryReactiveRepository salaryRepository,
                          Validator validator) {
        this.userRepository = userRepository;
        this.salaryRepository = salaryRepository;
        this.validator = validator;
    }

    /**
     * controller handler for GET endpoint available only to authenticated users /api/empl/payment, that gives access to
     * the payment data for the authenticated user. -> in stage 2 (tempor.) returns only the user data...
     * @param request the ServerRequest containing the user's principal data.
     * @return an EmployeeResponse object
     */
    public Mono<ServerResponse> accessPayrolls(ServerRequest request) {
        return request.principal()
                .flatMap(principal -> ok()
                        .body(userRepository.findByEmail(principal.getName()).map(Login::createEmployeeResponse),
                                EmployeeResponse.class));
    }

    @Transactional
    public Mono<ServerResponse> uploadPayrolls(ServerRequest request) {
        return request.bodyToFlux(SalaryRecord.class)
                .index().flatMap(this::validate)
                .collectList()
                .flatMap(list -> ok().body(saveSalaryRecord(list), StatusResponse.class));
    }

    public Mono<ServerResponse> changePayrolls(ServerRequest ignoredRequest) {
        return ok().bodyValue("Implement me!");
    }

    /**
     * hibernate validate and save to database
     * @param tuple request data to validate and save
     * @return a signup response as Mono if new user saved, error Mono else.
     */
    private Mono<Tuple2<SalaryRecord,String>> validate(Tuple2<Long, SalaryRecord> tuple) {
        Errors errors = new BeanPropertyBindingResult(tuple.getT2(), SalaryRecord.class.getName());
        validator.validate(tuple.getT2(), errors);
        Mono<String> errorMono = Mono.just(errors.hasErrors() ? "Record %d: ".formatted(tuple.getT1())
                + errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" && ")) : "");
        if (!errors.hasErrors()) {
            errorMono = validateWithDatabase(tuple.getT1(), tuple.getT2());
        }
        return Mono.just(tuple.getT2()).zipWith(errorMono);
    }

    private Mono<String> validateWithDatabase(long recordId, SalaryRecord salaryRecord) {
        return userRepository.findByEmail(salaryRecord.employee())
                .hasElement()
                .flatMap(hasUserElement -> {
                    if (TRUE.equals(hasUserElement)) {
                        return salaryRepository
                                .findByEmployeeAndPeriod(salaryRecord.employee(), salaryRecord.period())
                                .hasElement()
                                .map(hasSalaryElement -> TRUE.equals(hasSalaryElement) ?
                                        "Record %d: %s".formatted(recordId, RECORD_ALREADY_EXISTS_ERRORMSG) : "");
                    } else {
                        return Mono.just("Record %d: %s".formatted(recordId, NO_SUCH_EMPLOYEE_ERRORMSG));
                    }});
    }

    private Mono<StatusResponse> saveSalaryRecord(List<Tuple2<SalaryRecord, String>> tuples) {
        if (tuples.stream().anyMatch(not(tuple -> tuple.getT2().isEmpty()))) {
            String joinedErrorMessage = tuples.stream().map(Tuple2::getT2)
                    .filter(not(String::isEmpty)).collect(Collectors.joining(" | "));
            return Mono.error(new ServerWebInputException(joinedErrorMessage));
        }
        if (tuples.stream()
                .map(Tuple2::getT1)
                .collect(Collectors.groupingBy(salRec -> salRec.employee().toLowerCase()+salRec.period()))
                .values().stream()
                .map(List::size)
                .anyMatch(s -> s > 1)) {
            return Mono.error(new ServerWebInputException(DUPLICATE_RECORDS_ERRORMSG));
        }
        return salaryRepository.saveAll(tuples.stream().map(Tuple2::getT1).map(Salary::fromSalaryRecord).toList())
                .count()
                .map(count -> new StatusResponse("%d records %s".formatted(count,ADDED_SUCCESSFULLY)));
    }
}
