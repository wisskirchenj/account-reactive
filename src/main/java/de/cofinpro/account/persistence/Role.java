package de.cofinpro.account.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class connected to R2DBC-Table ROLES, that contains the available roles Strings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("ROLES")
public class Role {

    @Id
    private long id;
    @Column("USER_ROLE")
    private String roleName;
}
