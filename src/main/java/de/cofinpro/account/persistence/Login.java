package de.cofinpro.account.persistence;

import de.cofinpro.account.authentication.SignupRequest;
import de.cofinpro.account.authentication.SignupResponse;
import de.cofinpro.account.domain.EmployeeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entity class connected to R2DBC-Table LOGIN, that implements the UserDetails interface of Spring Security.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LOGIN")
public class Login implements UserDetails {

    private static final Login UNKNOWN = Login.builder().id(-1).build();

    @Id
    private long id;
    private String name;
    private String lastname;
    private String email;
    private String password;

    @Builder.Default()
    @Transient
    private List<String> roles = new ArrayList<>();

    /**
     * factory method from request and encrypted password.
     * @return entity instance with given data
     */
    public static Login fromSignupRequest(SignupRequest request, String encryptedPassword) {
        return Login.builder().name(request.name()).lastname(request.lastname()).email(request.email())
                .password(encryptedPassword).build();
    }

    /**
     * create an EmployeeResponse from a data base retrieved login user
     * @param userDetails user details found via principal's name
     * @return newly created response object
     */
    public static EmployeeResponse createEmployeeResponse(UserDetails userDetails) {
        Login user = (Login) userDetails;
        return new EmployeeResponse(user.id, user.name, user.lastname, user.email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * map a saved Login entity into an appropriate response for the signup
     */
    public SignupResponse toSignupResponse() {
        return new SignupResponse(id, name, lastname, email);
    }

    /**
     * return the static UNKNOWN Login instance - used in the save process as async Mono body.
     * @return the "unknown"-tagged Login instance
     */
    public static Login unknown() {
        return UNKNOWN;
    }

    /**
     * checker method, if a Login ionstance is "unknown".
     * @return true iff it is unknown.
     */
    public boolean isUnknown() {
        return id == -1;
    }
}
