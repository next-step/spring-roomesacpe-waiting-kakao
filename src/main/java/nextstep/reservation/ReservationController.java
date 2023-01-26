package nextstep.reservation;

import auth.AuthenticationException;
import auth.LoginMember;
import auth.UserDetails;
import nextstep.member.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class ReservationController {

    public final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity createReservation(@LoginMember UserDetails userDetails, @RequestBody ReservationRequest reservationRequest) {
        checkValid(userDetails);
        Long id = reservationService.create(Member.of(userDetails), reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + id)).build();
    }

    @GetMapping("/reservations")
    public ResponseEntity readReservations(@RequestParam Long themeId, @RequestParam String date) {
        List<Reservation> results = reservationService.findAllByThemeIdAndDate(themeId, date);
        return ResponseEntity.ok().body(results);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity deleteReservation(@LoginMember UserDetails userDetails, @PathVariable Long id) {
        checkValid(userDetails);
        reservationService.deleteById(Member.of(userDetails), id);

        return ResponseEntity.noContent().build();
    }

    private static void checkValid(UserDetails userDetails) {
        if(Objects.isNull(userDetails)){
            throw new AuthenticationException();
        }
    }
}
