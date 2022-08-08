package de.cofinpro.account.persistence;

import de.cofinpro.account.domain.SalaryRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class connected to R2DBC-Table SALARY, that stores employee salary record to a month period.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("SALARY")
public class Salary {

    @Id
    private long id;
    private String email;
    private String period;
    @Column("salary")
    private long monthlySalary;

    public static Salary fromSalaryRecord(SalaryRecord salaryRecord) {
        return Salary.builder().email(salaryRecord.employee()).monthlySalary(salaryRecord.salary())
                .period(salaryRecord.period()).build();
    }
}
