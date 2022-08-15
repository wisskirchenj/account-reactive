package de.cofinpro.account.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * immutable record received via http POST and PUT containing a salary record for an employee and month = period
 */
public record SalaryRecord(
        @NotNull @Pattern(regexp ="(?i)\\w+(\\.\\w+){0,2}@acme.com", message = "Not a valid corporate Email") String employee,
        @NotNull @Pattern(regexp = "(0[1-9]|1[0-2])-[1-9]\\d{3}", message = "Wrong date!") String period,
        @Min(value = 0, message = "Salary must be non negative!") long salary) {
}
