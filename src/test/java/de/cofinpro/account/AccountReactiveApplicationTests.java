package de.cofinpro.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest(excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@AutoConfigureWebTestClient
class AccountReactiveApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    void whenInvalidPath_ThenNotFoundReturned() {
        webClient
                .get().uri("/invalid")
                .exchange()
                .expectStatus().isNotFound();
    }
}
