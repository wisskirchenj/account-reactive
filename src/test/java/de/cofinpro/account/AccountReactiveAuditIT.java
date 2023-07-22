package de.cofinpro.account;

import de.cofinpro.account.admin.LockUserToggleRequest;
import de.cofinpro.account.admin.RoleToggleRequest;
import de.cofinpro.account.audit.AuditEventResponse;
import de.cofinpro.account.authentication.ChangepassRequest;
import de.cofinpro.account.authentication.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.stream.IntStream;

import static de.cofinpro.account.AccountReactiveAuthenticationIT.signup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest()
@AutoConfigureWebTestClient
class AccountReactiveAuditIT {

    static boolean usersSignedUp = false;

    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setup() {
        if (!usersSignedUp) {
            signup(webClient, new SignupRequest("system", "admin", "admin@acme.com", "attminattmin"));
            signup(webClient, new SignupRequest("Hans", "Wurst", "hw2@acme.com", "useruseruser"));
            usersSignedUp = true;
        }
    }

    @Test
    void whenSignup_CreateUserLogged() {
        giveAuditorRole();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> list[0].action(), equalTo("CREATE_USER"))
                .value(list -> list[0].id(), equalTo(1L))
                .value(list -> list[0].subject(), equalTo("Anonymous"))
                .value(list -> list[0].object(), equalTo("admin@acme.com"))
                .value(list -> list[0].path(), equalTo("/api/auth/signup"))
                .value(list -> list[1].action(), equalTo("CREATE_USER"));
    }

    @Test
    void whenGrantRemoveRole_GrantRemoveRoleLogged() {
        giveAuditorRole();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list).map(AuditEventResponse::object)
                        .anyMatch("Grant role AUDITOR to hw2@acme.com"::equals), equalTo(true));
        webClient.put().uri("/api/admin/user/role")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .bodyValue(new RoleToggleRequest("hw2@acme.com", "AUDITOR", "remove"))
                .exchange();
        giveAuditorRole();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list).map(AuditEventResponse::object)
                        .anyMatch("Remove role AUDITOR from hw2@acme.com"::equals), equalTo(true));
    }

    @Test
    void whenBadCredentials_LoginFailedLogged() {
        giveAuditorRole();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "usersseruser"))
                .exchange();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list).map(AuditEventResponse::action).anyMatch("LOGIN_FAILED"::equals), equalTo(true));
    }

    @Test
    void whenDeleteUser_DeleteUserLogged() {
        giveAuditorRole();
        signup(webClient, new SignupRequest("A", "B", "ab@acme.com", "123456123456"));
        webClient.delete().uri("/api/admin/user/ab@acme.com")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange().expectStatus().isOk();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list).map(AuditEventResponse::action).anyMatch("DELETE_USER"::equals), equalTo(true));
    }

    @Test
    void whenChangePassword_ChangePasswordLogged() {
        giveAuditorRole();
        signup(webClient, new SignupRequest("A", "C", "ac@acme.com", "123456123456"));
        webClient.post().uri("/api/auth/changepass")
                .headers(headers -> headers.setBasicAuth("ac@acme.com", "123456123456"))
                .bodyValue(new ChangepassRequest("qwertzqwertz"))
                .exchange().expectStatus().isOk();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list)
                        .filter(resp -> "CHANGE_PASSWORD".equals(resp.action())).findFirst()
                        .orElseThrow().subject(), equalTo("ac@acme.com"));
    }

    @Test
    void whenUnauthorizedLogin_AccessDeniedLogged() {
        giveAuditorRole();
        webClient.get().uri("/api/empl/payment")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .exchange().expectStatus().isForbidden();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list)
                        .filter(resp -> "ACCESS_DENIED".equals(resp.action()) && "admin@acme.com".equals(resp.subject())).findFirst()
                        .orElseThrow().path(), equalTo("/api/empl/payment"));
    }

    @Test
    void when5Unauthenticated_BrutForceLogged() {
        giveAuditorRole();
        signup(webClient, new SignupRequest("A", "D", "ad@acme.com", "123456123456"));
        IntStream.rangeClosed(1, 5).forEach(n -> webClient.get().uri("/api/empl/payment")
                .headers(headers -> headers.setBasicAuth("ad@acme.com", "12345612345678"))
                .exchange());
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list)
                        .filter(resp -> "BRUTE_FORCE".equals(resp.action())).findFirst()
                        .orElseThrow().subject(), equalTo("ad@acme.com"))
                .value(list -> Arrays.stream(list)
                        .filter(resp -> "LOCK_USER".equals(resp.action()))
                        .filter(resp -> "ad@acme.com".equals(resp.subject())).findFirst()
                        .orElseThrow().object(), equalTo("Lock user ad@acme.com"));
    }

    @Test
    void whenUserUnlocked_UnlockUserLogged() {
        giveAuditorRole();
        signup(webClient, new SignupRequest("A", "E", "ae@acme.com", "123456123456"));
        webClient.put().uri("/api/admin/user/access")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .bodyValue(new LockUserToggleRequest("ae@acme.com", "lock"))
                .exchange();
        webClient.put().uri("/api/admin/user/access")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .bodyValue(new LockUserToggleRequest("ae@acme.com", "UNlock"))
                .exchange();
        webClient.get().uri("/api/security/events")
                .headers(headers -> headers.setBasicAuth("hw2@acme.com", "useruseruser"))
                .exchange().expectStatus().isOk()
                .expectBody(AuditEventResponse[].class)
                .value(list -> Arrays.stream(list)
                        .filter(resp -> "UNLOCK_USER".equals(resp.action()))
                        .filter(resp -> "Unlock user ae@acme.com".equals(resp.object()))
                        .findFirst()
                        .orElseThrow(), notNullValue());
    }

    void giveAuditorRole() {
        webClient.put().uri("/api/admin/user/role")
                .headers(headers -> headers.setBasicAuth("admin@acme.com", "attminattmin"))
                .bodyValue(new RoleToggleRequest("hw2@acme.com", "AUDITOR", "GRANT"))
                .exchange();
    }
}
