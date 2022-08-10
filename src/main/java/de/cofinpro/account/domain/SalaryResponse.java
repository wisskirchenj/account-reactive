package de.cofinpro.account.domain;

import de.cofinpro.account.persistence.Login;
import de.cofinpro.account.persistence.Salary;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public record SalaryResponse(String name, String lastname, String period, String salary) {

    public static SalaryResponse fromLoginAndSalary(Salary salary, Login login) {
        return new SalaryResponse(login.getName(), login.getLastname(),
                monthFirst(salary.getPeriod()), getSalaryText(salary.getMonthlySalary()));
    }

    private static String getSalaryText(long salary) {
        return "%d dollar(s) %02d cent(s)".formatted(salary / 100, salary % 100);
    }

    private static String monthFirst(String period) {
       return Month.of(Integer.parseInt(period.substring(5)))
               .getDisplayName(TextStyle.FULL, Locale.US) +
                "-" + period.substring(0, 4);
    }
}
