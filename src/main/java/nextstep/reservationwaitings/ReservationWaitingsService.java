package nextstep.reservationwaitings;

import nextstep.member.Member;
import nextstep.reservation.Reservation;
import nextstep.reservation.ReservationDao;
import nextstep.schedule.Schedule;
import nextstep.schedule.ScheduleDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationWaitingsService {

    private final ReservationWaitingsDao reservationWaitingsDao;
    private final ScheduleDao scheduleDao;
    private final ReservationDao reservationDao;

    public ReservationWaitingsService(ReservationWaitingsDao reservationWaitingsDao, ScheduleDao scheduleDao, ReservationDao reservationDao) {
        this.reservationWaitingsDao = reservationWaitingsDao;
        this.scheduleDao = scheduleDao;
        this.reservationDao = reservationDao;
    }

    public ReservationResult create(Member member, ReservationWaitingRequest request) {
        Schedule schedule = scheduleDao.findById(request.getScheduleId());
        long waitNum = reservationWaitingsDao.findWaitNumByScheduleId(schedule.getId());
        if (waitNum == 0) {
            Reservation reservation = new Reservation(schedule, member);
            long id = reservationDao.save(reservation);
            return ReservationResult.createReservation(id);
        }
        ReservationWaitings reservationWaitings = new ReservationWaitings(member, schedule, waitNum + 1);
        long id = reservationWaitingsDao.create(reservationWaitings);
        return ReservationResult.createReservationWaiting(id);
    }

    public List<ReservationWaitings> findMyReservationWaitings(Member member) {
        return reservationWaitingsDao.findMyReservationWaitings(member.getId());
    }
}
