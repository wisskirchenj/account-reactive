package de.cofinpro.account.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class connected to R2DBC-Table LOGIN_ROLES, that contains the association of roles to users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LOGIN_ROLES")
public class LoginRole {

    @Id
    private long id;
    private String email;
    @Column("USER_ROLE")
    private String role;
}