package de.cofinpro.account.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationHandlerValidatorUnitTest {

    Validator validator;

    @BeforeEach
    void setup() {
        if (validator == null) {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                this.validator = validatorFactory.getValidator();
            }
        }
    }

    @ParameterizedTest
    @MethodSource
    void signupInvalidRequests(SignupRequest signupRequest) {
        assertEquals(1, validator.validate(signupRequest).size());
    }

    static Stream<Arguments> signupInvalidRequests() {
        return Stream.of(
                Arguments.of(new SignupRequest("", "Seeler", "t.s@xy.de", "123456")),
                Arguments.of(new SignupRequest(null, "Seeler", "t.s@xy.de", "123456")),
                Arguments.of(new SignupRequest("Toni", "", "t.s@xy.de", "123456")),
                Arguments.of(new SignupRequest("Toni", null, "t.s@xy.de", "123456")),
                Arguments.of(new SignupRequest("Toni", "Seeler", "", "123456")),
                Arguments.of(new SignupRequest("Toni", "Seeler", null, "123456")),
                Arguments.of(new SignupRequest("Toni", "Seeler", "t.s@xy.de", "")),
                Arguments.of(new SignupRequest("Toni", "Seeler", "t.s@xy.de", null))
        );
    }
}
