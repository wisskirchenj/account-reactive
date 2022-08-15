package de.cofinpro.account.configuration;

import de.cofinpro.account.persistence.Role;
import de.cofinpro.account.persistence.RoleReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class AdminConfiguration {

    @Bean
    @Autowired
    public List<Role> getRoles(RoleReactiveRepository roles) {
        return roles.findAll().collectList().block(Duration.ofMillis(200));
    }

    public static final String DELETED_SUCCESSFULLY = "Deleted successfully!";
    public static final String USER_NOT_FOUND_ERRORMSG = "User not found!";
    public static final String CANT_DELETE_ADMIN_ERRORMSG = "Can't remove ADMINISTRATOR role!";
}
