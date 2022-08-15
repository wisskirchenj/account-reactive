package de.cofinpro.account;

import de.cofinpro.account.admin.UserDeletedResponse;
import de.cofinpro.account.authentication.SignupRequest;
import de.cofinpro.account.authentication.SignupResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.List;

import static de.cofinpro.account.configuration.AdminConfiguration.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureWebTestClient
class AccountReactiveAdminIT {

    static boolean usersSignedUp = false;

    @Autowired
    WebTestClient webClient;

    @BeforeAll
    static void dbSetup() throws IOException {
        AccountReactiveAuthenticationIT.dbSetup();
    }

    @BeforeEach
    void setup() {
        if (!usersSignedUp) {
            usersSignedUp = true;
            signup(new SignupRequest("system", "admin", "admin@acme.com", "attminattmin"));
            signup(new SignupRequest("Hans", "Wurst", "hw@acme.com", "useruseruser"));
        }
    }

    @Test
    void whenAdminAuthenticatedDeleteNoPath_Then404() {
        webClient.delete().uri("/api/admin/user")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenAdminAuthenticatedInvalidEmail_Then400() {
        webClient.delete().uri("/api/admin/user/a@acmecom")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"message\": \"Invalid user email given: 'a@acmecom'!\"}");
    }

    @Test
    void whenAdminAuthenticatedUserNotExists_Then404() {
        webClient.delete().uri("/api/admin/user/a@acme.com")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json("{\"message\": \"" + USER_NOT_FOUND_ERRORMSG + "\"}");
    }

    @Test
    void whenAdminAuthenticatedDeleteAdmin_Then400() {
        webClient.delete().uri("/api/admin/user/admin@acme.com")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("{\"message\": \"" + CANT_DELETE_ADMIN_ERRORMSG + "\"}");
    }

    @Test
    void whenAdminAuthenticatedDeleteUser_ThenOkAndDeleteStatusReturned() {
        signup(new SignupRequest("Anton", "Wurst", "aw@acme.com", "useruseruser"));
        webClient.delete().uri("/api/admin/user/aw@acme.com")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDeletedResponse.class)
                .value(UserDeletedResponse::user, equalTo("aw@acme.com"))
                .value(UserDeletedResponse::status, equalTo(DELETED_SUCCESSFULLY));
    }

    @Test
    void whenAdminAuthenticated_ThenUsersReturned() {
        webClient.get().uri("/api/admin/user")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SignupResponse[].class)
                .value(list -> list.length, equalTo(2))
                .value(list -> list[0].id(), equalTo(1L))
                .value(list -> list[1].id(), equalTo(2L))
                .value(list -> list[0].roles(), equalTo(List.of("ROLE_ADMINISTRATOR")))
                .value(list -> list[1].roles(), equalTo(List.of("ROLE_USER")))
                .value(list -> list[0].email(), equalTo("admin@acme.com"))
                .value(list -> list[1].email(), equalTo("hw@acme.com"))
                .value(list -> list[0].name(), equalTo("system"));
    }

    @Test
    void whenUserRoleAuthenticated_ThenAdminEndpointsReturn403() {
        webClient.get().uri("/api/admin/user")
                .headers(headers -> headers.setBasicAuth("hw@acme.com", "useruseruser"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .json("{\"message\": \"Access Denied!\"}");
        webClient.put().uri("/api/admin/user/role")
                .headers(headers -> headers.setBasicAuth("hw@acme.com", "useruseruser"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .json("{\"message\": \"Access Denied!\"}");
        webClient.delete().uri("/api/admin/user/a@acme.com")
                .headers(headers -> headers.setBasicAuth("hw@acme.com", "useruseruser"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .json("{\"message\": \"Access Denied!\"}");
    }

    void signup(SignupRequest request) {
        webClient.post().uri("/api/auth/signup")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }
}