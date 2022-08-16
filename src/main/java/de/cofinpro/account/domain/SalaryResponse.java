package de.cofinpro.account.domain;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.Salary;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * immutable response object to be returned by the GET /api/empl/payment.
 */
public record SalaryResponse(String name, String lastname, String period, String salary) {

    /**
     * create the response from the 2 query result entities Login and Salary
     * @param salary entity
     * @param login entity
     */
    public static SalaryResponse fromLoginAndSalary(Salary salary, Login login) {
        return new SalaryResponse(login.getName(), login.getLastname(),
                monthFirst(salary.getPeriod()), getSalaryText(salary.getMonthlySalary()));
    }

    private static String getSalaryText(long salary) {
        return "%d dollar(s) %02d cent(s)".formatted(salary / 100, salary % 100);
    }

    /**
     * translate the database record to textual representation (e.g. 2021-05 -> May-2021)
     */
    private static String monthFirst(String period) {
       return Month.of(Integer.parseInt(period.substring(5))).getDisplayName(TextStyle.FULL, Locale.US) +
                "-" + period.substring(0, 4);
    }
}