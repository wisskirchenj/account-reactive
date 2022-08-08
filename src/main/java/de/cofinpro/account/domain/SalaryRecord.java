package de.cofinpro.account.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public record SalaryRecord(
        @NotEmpty @Pattern(regexp ="(?i)\\w+(\\.\\w+){0,2}@acme.com", message = "Not a valid corporate Email") String employee,
        @NotEmpty @Pattern(regexp = "(0[1-9]|1[0-2])-[1-9]\\d{3}", message = "Wrong date!") String period,
        @Min(value = 0, message = "Salary must be non negative!") long salary) {
}
