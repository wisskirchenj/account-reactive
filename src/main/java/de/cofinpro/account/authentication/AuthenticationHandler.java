package de.cofinpro.account.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import org.springframework.validation.Validator;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
@Slf4j
public class AuthenticationHandler {

    private final Validator validator;

    @Autowired
    public AuthenticationHandler(Validator validator) {
        this.validator = validator;
    }

    public Mono<ServerResponse> signup(ServerRequest request) {
        return request.bodyToMono(SignupRequest.class)
                .doOnSuccess(signupRequest -> log.info(signupRequest.toString()))
                .map(this::validate)
                .map(SignupRequest::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    private SignupRequest validate(SignupRequest signupRequest) {
        Errors errors = new BeanPropertyBindingResult(signupRequest, SignupRequest.class.getName());
        validator.validate(signupRequest, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.getAllErrors().toString());
        }
        return signupRequest;
    }

    public Mono<ServerResponse> changePassword(ServerRequest ignoredServerRequest) {
        return ok().build();
    }
}
