package nextstep.reservation;

import auth.AuthenticationException;
import nextstep.member.Member;
import nextstep.member.MemberDao;
import nextstep.reservationwaiting.ReservationWaiting;
import nextstep.reservationwaiting.ReservationWaitingDao;
import nextstep.schedule.Schedule;
import nextstep.schedule.ScheduleDao;
import nextstep.support.DuplicateEntityException;
import nextstep.theme.Theme;
import nextstep.theme.ThemeDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    public final ReservationDao reservationDao;
    public final ThemeDao themeDao;
    public final ScheduleDao scheduleDao;
    public final MemberDao memberDao;
    private final ReservationWaitingDao reservationWaitingDao;

    public ReservationService(ReservationDao reservationDao, ThemeDao themeDao, ScheduleDao scheduleDao, MemberDao memberDao, ReservationWaitingDao reservationWaitingDao) {
        this.reservationDao = reservationDao;
        this.themeDao = themeDao;
        this.scheduleDao = scheduleDao;
        this.memberDao = memberDao;
        this.reservationWaitingDao = reservationWaitingDao;
    }

    public Long create(Member member, ReservationRequest reservationRequest) {
        if (member == null) {
            throw new AuthenticationException();
        }
        Schedule schedule = scheduleDao.findById(reservationRequest.getScheduleId());
        if (schedule == null) {
            throw new NullPointerException();
        }

        List<Reservation> reservation = reservationDao.findByScheduleId(schedule.getId());
        if (!reservation.isEmpty()) {
            throw new DuplicateEntityException();
        }

        Reservation newReservation = new Reservation(
                schedule,
                member
        );

        return reservationDao.save(newReservation);
    }

    public List<Reservation> findAllByThemeIdAndDate(Long themeId, String date) {
        Theme theme = themeDao.findById(themeId);
        if (theme == null) {
            throw new NullPointerException();
        }

        return reservationDao.findAllByThemeIdAndDate(themeId, date);
    }

    public List<Reservation> findAllByMemberId(Member member) {
        if (member == null) {
            throw new AuthenticationException();
        }

        return reservationDao.findAllByMemberId(member.getId());
    }

    public void deleteById(Member member, Long id) {
        Reservation reservation = reservationDao.findById(id);
        if (reservation == null) {
            throw new NullPointerException();
        }

        if (!reservation.sameMember(member)) {
            throw new AuthenticationException();
        }

        reservationDao.deleteById(id);

        ReservationWaiting reservationWaiting = reservationWaitingDao.findByScheduleId(reservation.getSchedule().getId());

        if (reservationWaiting == null) {
            return;
        }

        reservationWaitingDao.deleteById(reservationWaiting.getId());

        reservationDao.save(Reservation.of(reservationWaiting));
    }
}
