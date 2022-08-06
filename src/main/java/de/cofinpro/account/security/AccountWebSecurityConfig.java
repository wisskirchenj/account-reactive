package de.cofinpro.account.security;

import de.cofinpro.account.configuration.AuthenticationConfiguration;
import de.cofinpro.account.persistence.LoginReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Spring WebFlux security configuration, that sets up the Security WebFilterChain with access information to
 * the endpoints, Http-Basic authentication manager and error handling and CSRF disabling.
 * Further, beans provide the ReactiveUserDetailsService and a BcryptPasswordEncoder for use in the authentication
 * manager.
 */
@EnableWebFluxSecurity
public class AccountWebSecurityConfig {

    @Bean
    @Autowired
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            ReactiveAuthenticationManager authenticationManager) {
        http.csrf().disable()
                .httpBasic(httpBasicSpec -> httpBasicSpec
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage())))
                        .authenticationManager(authenticationManager)
                )
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST,"/api/auth/signup").permitAll()
                .pathMatchers(HttpMethod.GET,"/actuator", "/actuator/**").permitAll()
                .pathMatchers("/api/**").authenticated()
                .and().formLogin();
        return http.build();
    }

    /**
     * UserDetailsService bean, that just delegates the retrieval to the LoginReactiveRepository
     * (functional interface implementation of the findByUsername - method).
     * NOTE: the bean is instantiated by the Spring framework internally...
     * @param users the Login Reactive repository
     * @return UserDetailsService instance (anonymous via method-reference).
     */
    @Bean
    @Autowired
    public ReactiveUserDetailsService userDetailsService(LoginReactiveRepository users) {
        return users::findByEmail;
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(AuthenticationConfiguration.BCRYPT_STRENGTH);
    }
}
