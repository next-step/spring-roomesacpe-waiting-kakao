package nextstep.reservationwaiting;

import auth.AuthenticationException;
import nextstep.member.Member;
import nextstep.member.MemberDao;
import nextstep.schedule.Schedule;
import nextstep.schedule.ScheduleDao;
import nextstep.support.DuplicateEntityException;
import nextstep.theme.Theme;
import nextstep.theme.ThemeDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationWaitingService {
    public final ReservationWaitingDao reservationWaitingDaoDao;

    public ReservationWaitingService(ReservationWaitingDao reservationWaitingDaoDao) {
        this.reservationWaitingDaoDao = reservationWaitingDaoDao;
    }

//    public Long create(Member member, ReservationRequest reservationRequest) {
//        if (member == null) {
//            throw new AuthenticationException();
//        }
//        Schedule schedule = scheduleDao.findById(reservationRequest.getScheduleId());
//        if (schedule == null) {
//            throw new NullPointerException();
//        }
//
//        List<Reservation> reservation = reservationDao.findByScheduleId(schedule.getId());
//        if (!reservation.isEmpty()) {
//            throw new DuplicateEntityException();
//        }
//
//        Reservation newReservation = new Reservation(
//                schedule,
//                member
//        );
//
//        return reservationDao.save(newReservation);
//    }
//
//    public List<Reservation> findAllByThemeIdAndDate(Long themeId, String date) {
//        Theme theme = themeDao.findById(themeId);
//        if (theme == null) {
//            throw new NullPointerException();
//        }
//
//        return reservationDao.findAllByThemeIdAndDate(themeId, date);
//    }
//
//    public void deleteById(Member member, Long id) {
//        Reservation reservation = reservationDao.findById(id);
//        if (reservation == null) {
//            throw new NullPointerException();
//        }
//
//        if (!reservation.sameMember(member)) {
//            throw new AuthenticationException();
//        }
//
//        reservationDao.deleteById(id);
//    }
}