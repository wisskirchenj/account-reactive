package de.cofinpro.account;

import de.cofinpro.account.authentication.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.hamcrest.CoreMatchers.is;


@SpringBootTest
@AutoConfigureWebTestClient
class AccountReactiveApplicationTests {

    @Autowired
    WebTestClient webClient;

    @Test
    @WithMockUser
    void whenChangepassAuthenticated_ThenOkReturned() {
        webClient.post().uri("/api/auth/changepass")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenChangepassUnauthenticated_Then401Returned() {
        webClient.post().uri("/api/auth/changepass")
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenSignup_ThenOkReturned() {
        webClient.post().uri("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new SignupRequest("Müller", "John", "j.m@a.de", "secret")))
                .exchange()
                .expectStatus().isOk();
    }
}
