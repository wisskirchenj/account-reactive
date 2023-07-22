package de.cofinpro.account.configuration;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class ObservabilityConfiguration {

    public static <T> Mono<T> extractAndLog(ServerRequest request, Class<T> bodyClass) {
        var logger = LoggerFactory.getLogger(bodyClass);
        var user = MDC.get("user");
        return request
                .bodyToMono(bodyClass)
                .doOnNext(body -> logger.info("{} {} {} with payload '{}'",
                        user,
                        request.method().name(),
                        request.path(),
                        body));
    }

    @Bean
    ObservabilityFilter observabilityFilter() {
        return new ObservabilityFilter();
    }

    static class ObservabilityFilter implements WebFilter {

        @Override
        @NonNull
        public Mono<Void> filter(@NonNull ServerWebExchange serverWebExchange, @NonNull WebFilterChain webFilterChain) {
            return ReactiveSecurityContextHolder.getContext().doOnNext(sc -> {
                if (sc.getAuthentication() != null) {
                    MDC.put("user", "<" + sc.getAuthentication().getName() + ">");
                }
            }).then(webFilterChain.filter(serverWebExchange));
        }
    }
}