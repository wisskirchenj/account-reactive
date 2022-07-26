package de.cofinpro.account.exception;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class SignupErrorResponse {

    private final LocalDate timestamp = LocalDate.now();
    private final int status = HttpStatus.BAD_REQUEST.value();
    private final String error = "Bad Request";
    private final String path;

    public SignupErrorResponse(String path) {
        this.path = path;
    }
}