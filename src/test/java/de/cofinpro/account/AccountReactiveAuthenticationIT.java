package de.cofinpro.account;

import de.cofinpro.account.authentication.ChangepassRequest;
import de.cofinpro.account.authentication.ChangepassResponse;
import de.cofinpro.account.authentication.SignupRequest;
import de.cofinpro.account.authentication.SignupResponse;
import de.cofinpro.account.domain.EmployeeResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.cofinpro.account.configuration.AuthenticationConfiguration.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
class AccountReactiveAuthenticationIT {

    @Autowired
    WebTestClient webClient;

    private static final Path TEST_DB_PATH = Path.of("./src/test/resources/data/test_db.mv.db");

    @BeforeAll
    static void dbSetup() throws IOException {
        Files.deleteIfExists(TEST_DB_PATH);
        Files.copy(Path.of("./src/test/resources/data/account_template.mv.db"), TEST_DB_PATH);
    }

    @Test
    void whenGetPaymentUnauthenticated_Then401Returned() {
        webClient.post().uri("/api/empl/payment")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().json("{\"message\": \"Not Authenticated\"}");
    }

    @Test
    void whenSignup_ThenOkAndResponseReturned() {
        webClient.post().uri("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SignupRequest("Müller", "John", "j.m@acme.COM", "secretsecret"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SignupResponse.class)
                .value(signupResponse -> assertTrue(signupResponse.id() > 0));
    }

    @Test
    void whenInvalidSignup_Then400Returned() {
        webClient.post().uri("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SignupRequest("Müller", "", "j.m@a.de", "secret"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenSignupTwiceSameEmailIgnoreCase_ThenUserExistsReturned() {
        signup(new SignupRequest("Peter", "John", "p.john@acme.com", "secretsecret"));
        webClient.post().uri("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SignupRequest("Peter2", "John", "P.JOHN@acme.com", "secretsecret"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().json("{\"message\": \""+ USER_EXISTS_ERRORMSG + "\"}");
    }

    @Test
    void whenSignedUpUserGetsPayment_ThenEmployeeResponseReturned() {
        signup(new SignupRequest("Hans", "Schmitz", "h.schmitz@acme.com", "secretsecret"));
        webClient.get().uri("/api/empl/payment")
                .headers(headers -> headers.setBasicAuth("h.schmitz@acme.com", "secretsecret"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeResponse.class)
                .value(response -> assertTrue(response.id() > 0))
                .value(EmployeeResponse::name, equalTo("Hans"))
                .value(EmployeeResponse::lastname, equalTo("Schmitz"))
                .value(EmployeeResponse::email, equalTo("h.schmitz@acme.com"));
    }

    @Test
    void whenSignedUpUserGetsPaymentBadCredentials_Then401Returned() {
        signup(new SignupRequest("Ulrich", "Weiss", "uweiss@acme.com", "secretsecret"));
        webClient.get().uri("/api/empl/payment")
                .headers(headers -> headers.setBasicAuth("uweiss@acme.com", "secret_secret"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().json("{\"message\": \"Invalid Credentials\"}");
    }

    @Test
    void stage3_example1() {
        webClient.post().uri("/api/auth/signup")
                .bodyValue(new SignupRequest("John", "Doe", "johnDoe@acme.com", "secret"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"error\": \"Bad Request\",\"message\": \"" + PASSWORD_TOO_SHORT_ERRORMSG +
                        "\",\"path\": \"/api/auth/signup\"}");
    }

    @Test
    void stage3_example2() {
        webClient.post().uri("/api/auth/signup")
                .bodyValue(new SignupRequest("John", "Doe", "johnDoe@acme.com", "PasswordForJune"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"error\": \"Bad Request\",\"message\": \"" + PASSWORD_HACKED_ERRORMSG +
                        "\", \"path\": \"/api/auth/signup\"}");
    }

    @Test
    void stage3_example3And4AndFurtherErrors() {
        signup(new SignupRequest("John", "Doe", "johnDoe@acme.com", "123456123456"));
        webClient.post().uri("/api/auth/changepass")
                .headers(headers -> headers.setBasicAuth("johnDoe@acme.com", "123456123456"))
                .bodyValue(new ChangepassRequest("bZPGqH7fW"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"error\": \"Bad Request\",\"message\": \"" + PASSWORD_TOO_SHORT_ERRORMSG +
                        "\", \"path\": \"/api/auth/changepass\"}");
        webClient.post().uri("/api/auth/changepass")
                .headers(headers -> headers.setBasicAuth("johnDoe@acme.com", "123456123456"))
                .bodyValue(new ChangepassRequest("PasswordForNovember"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"error\": \"Bad Request\",\"message\": \"" + PASSWORD_HACKED_ERRORMSG +
                        "\", \"path\": \"/api/auth/changepass\"}");
        webClient.post().uri("/api/auth/changepass")
                .headers(headers -> headers.setBasicAuth("johnDoe@acme.com", "123456123456"))
                .bodyValue(new ChangepassRequest("bZPGqH7fTJWW"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChangepassResponse.class)
                .value(ChangepassResponse::email, equalTo("johnDoe@acme.com"))
                .value(ChangepassResponse::status, equalTo(PASSWORD_UPDATEMSG));
        webClient.post().uri("/api/auth/changepass")
                .headers(headers -> headers.setBasicAuth("johnDoe@acme.com", "bZPGqH7fTJWW"))
                .body(BodyInserters.fromValue(new ChangepassRequest("bZPGqH7fTJWW")))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"error\": \"Bad Request\",\"message\": \"" + SAME_PASSWORD_ERRORMSG +
                        "\", \"path\": \"/api/auth/changepass\"}");
    }

    void signup(SignupRequest request) {
        webClient.post().uri("/api/auth/signup")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }
}
