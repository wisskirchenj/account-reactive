package de.cofinpro.account.authentication;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @deprecated 2022-07-26
 */
@Deprecated(since = "2022-07-26")
public class SignupRequestValidator implements Validator {

    private static final String NOT_EMPTY = "field is required and must not be empty";

    @Override
    public boolean supports(Class<?> clazz) {
        return SignupRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", NOT_EMPTY);
        ValidationUtils.rejectIfEmpty(errors, "lastname", NOT_EMPTY);
        ValidationUtils.rejectIfEmpty(errors, "email", NOT_EMPTY);
        ValidationUtils.rejectIfEmpty(errors, "password", NOT_EMPTY);
    }
}
