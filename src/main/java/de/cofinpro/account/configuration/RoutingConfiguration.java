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

    private RouterFunction<ServerResponse> accountRoutes(AccountHandler accountHandler) {
        return route()
                .GET("/api/empl/payment", accountHandler::accessPayrolls)
                .build();
    }
}
