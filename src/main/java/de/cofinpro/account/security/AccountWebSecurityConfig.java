package de.cofinpro.account.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class AccountWebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
                .httpBasic(httpBasicSpec -> httpBasicSpec
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.fromRunnable(() -> {throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");}))
                        //.authenticationManager()
                )
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                .pathMatchers(HttpMethod.POST,"/api/auth/signup").permitAll()
                .pathMatchers(HttpMethod.GET,"/actuator", "/actuator/**").permitAll()
                .and().formLogin();
        return http.build();
    }
}
