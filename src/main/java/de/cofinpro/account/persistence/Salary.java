package de.cofinpro.account.persistence;

import de.cofinpro.account.domain.SalaryRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class connected to R2DBC-Table SALARY, that stores employee salary record to a month period.
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("SALARY")
public class Salary {

    private static final Salary EMPTY = new Salary();

    @Id
    private long id;
    private String email;
    private String period;
    @Column("salary")
    private long monthlySalary;

    public static Salary fromSalaryRecord(SalaryRecord salaryRecord) {
        return Salary.builder().email(salaryRecord.employee()).monthlySalary(salaryRecord.salary())
                .period(yearFirst(salaryRecord.period())).build();
    }

    public static String yearFirst(String period) {
        return period.substring(3) + "-" + period.substring(0,2);
    }

    public static Salary empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }
}
