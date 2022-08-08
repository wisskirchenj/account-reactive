package de.cofinpro.account.configuration;

import de.cofinpro.account.domain.AccountHandler;
import de.cofinpro.account.authentication.AuthenticationHandler;
import de.cofinpro.account.admin.AdminHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * routing configuration (webflux.fn) defining the route-handler methods for all andpoints of account-reactive
 */
@Configuration
public class RoutingConfiguration {

    @Bean
    @Autowired
    public RouterFunction<ServerResponse> routes(AuthenticationHandler authenticationHandler,
                                                 AccountHandler accountHandler,
                                                 AdminHandler adminHandler) {
        return route().add(authenticationRoutes(authenticationHandler))
                .add(accountRoutes(accountHandler))
                //.add(adminRoutes(adminHandler))
                .build();
    }

    /**
     * route handling for the authentication specific routes
     * @param authenticationHandler handler
     */
    private RouterFunction<ServerResponse> authenticationRoutes(AuthenticationHandler authenticationHandler) {
        return route()
                .POST("/api/auth/signup", authenticationHandler::signup)
                .POST("/api/auth/changepass", authenticationHandler::changePassword)
                .build();
    }

    private RouterFunction<ServerResponse> adminRoutes(AdminHandler adminHandler) {
        return route()
                .build();
    }

    /**
     * route handling for all domain (account / payroll) specific routes
     * @param accountHandler handler
     */
    private RouterFunction<ServerResponse> accountRoutes(AccountHandler accountHandler) {
        return route()
                .GET("/api/empl/payment", accountHandler::accessPayrolls)
                .POST("/api/acct/payments", accountHandler::uploadPayrolls)
                .PUT("/api/acct/payments", accountHandler::changePayrolls)
                .build();
    }
}
