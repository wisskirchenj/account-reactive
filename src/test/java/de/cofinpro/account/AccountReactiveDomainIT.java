package de.cofinpro.account;

import de.cofinpro.account.authentication.SignupRequest;
import de.cofinpro.account.domain.SalaryRecord;
import de.cofinpro.account.domain.SalaryResponse;
import de.cofinpro.account.domain.StatusResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.List;

import static de.cofinpro.account.configuration.AccountConfiguration.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureWebTestClient
class AccountReactiveDomainIT {

    @Autowired
    WebTestClient webClient;

    @BeforeAll
    static void dbSetup() throws IOException {
        AccountReactiveAuthenticationIT.dbSetup();
    }

    @Test
    void whenInvalidSalaryRequest_Then400() {
        signup(new SignupRequest("Pete", "Doe", "p.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(new SalaryRecord("p.d@acme.com", "13-2022", 100))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo("Record 0: Wrong date!"))
                .jsonPath("$.path").value(equalTo("/api/acct/payments"));
    }


    @Test
    void whenInvalidPutSalaryRequest_Then400() {
        signup(new SignupRequest("Iota", "Doe", "i.d@acme.com", "123456789012"));
        webClient.put().uri("/api/acct/payments")
                .bodyValue(new SalaryRecord("i.d@acme.com", "13-2022", 5000))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo("Wrong date!"))
                .jsonPath("$.path").value(equalTo("/api/acct/payments"));
        webClient.put().uri("/api/acct/payments")
                .bodyValue(new SalaryRecord("notuser.d@acme.com", "06-2022", 5000))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo(NO_SUCH_SALES_RECORD_ERRORMSG));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(new SalaryRecord("i.d@acme.com", "06-2022", 5000))
                .exchange()
                .expectStatus().isOk();
        webClient.put().uri("/api/acct/payments")
                .bodyValue(new SalaryRecord("i.d@acme.com", "05-2022", 5000))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo(NO_SUCH_SALES_RECORD_ERRORMSG));
    }

    @Test
    void whenValidSalaryRequest_ThenOkAndStatusResponseReturned() {
        signup(new SignupRequest("Alf", "Doe", "a.d@acme.com", "123456789012"));
        signup(new SignupRequest("Gary", "Doe", "g.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("a.d@acme.com", "07-2022", 10000),
                        new SalaryRecord("g.d@acme.com", "06-2022", 5000)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StatusResponse.class)
                .value(StatusResponse::status, equalTo("2 records " + ADDED_SUCCESSFULLY));
    }

    @Test
    void whenValidSalaryRequestUserNotExists_Then400AndErrorMessage() {
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("not.there@acme.com", "06-2022", 5000)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo("Record 0: " + NO_SUCH_EMPLOYEE_ERRORMSG));
    }

    @Test
    void whenValidSalaryRequestSalaryExistsInDb_Then400AndErrorMessage() {
        signup(new SignupRequest("Beta", "Doe", "b.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("b.d@acme.com", "06-2022", 5000)))
                .exchange()
                .expectStatus().isOk();
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("b.d@acme.com", "05-2022", 5000),
                        new SalaryRecord("b.d@acme.com", "06-2022", 5000)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo("Record 1: " + RECORD_ALREADY_EXISTS_ERRORMSG));
    }

    @Test
    void whenDuplicateRecordsInRequest_Then400AndErrorMessage() {
        signup(new SignupRequest("Charlie", "Doe", "c.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("c.d@acme.com", "01-2022", 5000),
                        new SalaryRecord("c.d@acme.com", "02-2022", 1000),
                        new SalaryRecord("c.d@acme.com", "03-2022", 2000),
                        new SalaryRecord("c.d@acme.com", "05-2022", 4000),
                        new SalaryRecord("c.d@acme.com", "03-2022", 5000)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo(DUPLICATE_RECORDS_ERRORMSG));
    }

    @Test
    void whenTriplicateRecordsInRequest_Then400AndErrorMessage() {
        signup(new SignupRequest("Dave", "Doe", "d.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("d.d@acme.com", "03-2022", 5000),
                        new SalaryRecord("d.d@acme.com", "02-2022", 1000),
                        new SalaryRecord("d.d@acme.com", "03-2022", 2000),
                        new SalaryRecord("d.d@acme.com", "05-2022", 4000),
                        new SalaryRecord("d.d@acme.com", "03-2022", 5000)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(equalTo(DUPLICATE_RECORDS_ERRORMSG));
    }

    @Test
    void whenValidPutSalaryRequest_ThenOkAndStatusResponse() {
        signup(new SignupRequest("Hans", "Doe", "h.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("h.d@acme.com", "05-2022", 2000),
                        new SalaryRecord("h.d@acme.com", "07-2022", 2000),
                        new SalaryRecord("h.d@acme.com", "06-2022", 2000)))
                .exchange()
                .expectStatus().isOk();
        webClient.put().uri("/api/acct/payments")
                .bodyValue(new SalaryRecord("h.d@acme.com", "06-2022", 123456))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StatusResponse.class)
                .value(StatusResponse::status, equalTo(UPDATED_SUCCESSFULLY));
    }


    @Test
    void whenGetPayment_ThenSalaryResponseReturned() {
        signup(new SignupRequest("Jan", "Doe", "j.d@acme.com", "123456789012"));
        webClient.post().uri("/api/acct/payments")
                .bodyValue(List.of(
                        new SalaryRecord("j.d@acme.com", "05-2022", 5010),
                        new SalaryRecord("j.d@acme.com", "07-2022", 7025),
                        new SalaryRecord("j.d@acme.com", "06-2021", 6000)))
                .exchange()
                .expectStatus().isOk();
        webClient.get().uri(uri -> uri.path("/api/empl/payment").queryParam("period", "07-2022").build())
                .headers(headers -> headers.setBasicAuth("j.d@acme.com", "123456789012"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalaryResponse[].class)
                .value(list -> list[0].salary(), equalTo("70 dollar(s) 25 cent(s)"))
                .value(list -> list[0].name(), equalTo("Jan"))
                .value(list -> list[0].lastname(), equalTo("Doe"))
                .value(list -> list[0].period(), equalTo("July-2022"));
        webClient.get().uri("/api/empl/payment")
                .headers(headers -> headers.setBasicAuth("j.d@acme.com", "123456789012"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalaryResponse[].class)
                .value(list -> list[1].salary(), equalTo("50 dollar(s) 10 cent(s)"))
                .value(list -> list[0].salary(), equalTo("60 dollar(s) 00 cent(s)"))
                .value(list -> list[2].salary(), equalTo("70 dollar(s) 25 cent(s)"))
                .value(list -> list[0].name(), equalTo("Jan"))
                .value(list -> list[0].lastname(), equalTo("Doe"))
                .value(list -> list[1].period(), equalTo("May-2022"))
                .value(list -> list[2].period(), equalTo("July-2022"))
                .value(list -> list[0].period(), equalTo("June-2021"));
        webClient.get().uri(uri -> uri.path("/api/empl/payment").queryParam("period", "7-2022").build())
                .headers(headers -> headers.setBasicAuth("j.d@acme.com", "123456789012"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .value(equalTo("Wrong Date: Use mm-yyyy format!"));
    }

    void signup(SignupRequest request) {
        webClient.post().uri("/api/auth/signup")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }
}
