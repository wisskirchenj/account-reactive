package de.cofinpro.account;

import de.cofinpro.account.admin.LockUserToggleRequest;
import de.cofinpro.account.admin.RoleToggleRequest;
import de.cofinpro.account.admin.UserDeletedResponse;
import de.cofinpro.account.authentication.ChangepassRequest;
import de.cofinpro.account.authentication.ChangepassResponse;
import de.cofinpro.account.authentication.SignupRequest;
import de.cofinpro.account.authentication.SignupResponse;
import de.cofinpro.account.domain.SalaryRecord;
import de.cofinpro.account.domain.SalaryResponse;
import de.cofinpro.account.domain.StatusResponse;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints({ResourcesRegistrar.class})
@RegisterReflectionForBinding({SignupRequest.class,
        SignupResponse.class,
        ChangepassRequest.class,
        ChangepassResponse.class,
        LockUserToggleRequest.class,
        RoleToggleRequest.class,
        UserDeletedResponse.class,
        SalaryRecord.class,
        SalaryResponse.class,
        StatusResponse.class})
public class AccountReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountReactiveApplication.class, args);
    }

}
