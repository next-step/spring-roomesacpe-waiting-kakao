package nextstep.reservation_waiting;

import auth.TokenRequest;
import auth.TokenResponse;
import io.restassured.RestAssured;
import nextstep.AbstractE2ETest;
import nextstep.member.MemberRequest;
import nextstep.reservation.ReservationRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationWaitingE2ETest extends AbstractE2ETest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanUpTable() {
        jdbcTemplate.update("TRUNCATE TABLE reservation_waiting;");
    }

    @Test
    @DisplayName("예약 대기를 생성한다. (해당 스케줄에 예약이 없을 경우)")
    void createReservationWaitingWhenReservationAlreadyExists() {
        //given
        ReservationWaitingRequest request = new ReservationWaitingRequest(1L);

        //when
        var response = sendReservationWaiting(request);

        //then
        assertThat(response.header("Location").startsWith("/reservations/")).isTrue();
    }

    @Test
    @DisplayName("예약 대기를 생성한다. (해당 스케줄에 예약이 이미 있을 경우)")
    void createReservationWaiting() {
        //given
        createReservation(1L);
        ReservationWaitingRequest request = new ReservationWaitingRequest(1L);

        //when
        var response = sendReservationWaiting(request);

        //then
        assertThat(response.header("Location").startsWith("/reservation-waitings/")).isTrue();
    }

    private void createReservation(Long scheduleId) {
        ReservationRequest request = new ReservationRequest(scheduleId);

        RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    @Test
    @DisplayName("자신의 예약 대기를 조회할 수 있다.")
    void findMyReservationWaitings() {
        //given
        createReservation(1L);
        createReservation(2L);
        sendReservationWaiting(new ReservationWaitingRequest(1L));
        sendReservationWaiting(new ReservationWaitingRequest(2L));

        //when
        var response = RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/reservation-waitings/mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        //then
        List<ReservationWaitingResponse> responses = response.jsonPath().getList(".", ReservationWaitingResponse.class);
        assertThat(responses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("자신의 예약 대기를 취소할 수 있다.")
    void deleteMyReservationWaiting() {
        //given
        createReservation(1L);
        String location = sendReservationWaiting(new ReservationWaitingRequest(1L)).header("Location");

        //when & then
        RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    @DisplayName("자신의 예약 대기가 아니면 취소할 수 없다.")
    void deleteOtherReservationWaiting() {
        //given
        createReservation(1L);
        String location = sendReservationWaiting(new ReservationWaitingRequest(1L)).header("Location");
        String anotherToken = createAnotherMember();

        //when & then
        RestAssured
                .given().log().all()
                .auth().oauth2(anotherToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract();
    }

    private String createAnotherMember() {
        MemberRequest memberBody = new MemberRequest("AnotherUser", "AnotherUser", "user", "010-4321-5678", "USER");
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberBody)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        TokenRequest tokenBody = new TokenRequest("AnotherUser", "AnotherUser");
        var response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenBody)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        return response.as(TokenResponse.class).getAccessToken();
    }

    @Test
    @DisplayName("존재하지 않는 예약대기를 취소할 수 없다.")
    void deleteNotExistReservationWaiting() {
        //given
        String location = "/reservation-waitings/1";

        //when & then
        RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract();
    }
}
