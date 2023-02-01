package roomescape.service;

import errors.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.dto.ReservationsControllerPostBody;
import roomescape.entity.Waiting;
import roomescape.exception.ServiceException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class WaitingService {
    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;

    public String createWaiting(long callerId, ReservationsControllerPostBody body, Function<Long, String> onReservationCreated, Function<Long, String> onWaitingCreated) {
        var reservationId = reservationRepository.insert(body.getName(), body.getDate(), body.getTime(), body.getThemeId(), callerId);
        if (reservationId.isPresent()) {
            return onReservationCreated.apply(reservationId.get());
        }
        return onWaitingCreated.apply(waitingRepository.insert(body.getName(), body.getDate(), body.getTime(), body.getThemeId(), callerId));
    }

    @Transactional(readOnly = true)
    public List<Waiting> findMyWaiting(Long memberId) {
        return waitingRepository.selectByMemberId(memberId);
    }


    public void deleteWaiting(long callerId, Long id) {
        var waiting = waitingRepository.selectById(id);
        if (waiting.isEmpty()) {
            throw new ServiceException(ErrorCode.UNKNOWN_WAITING_ID);
        }
        if (waiting.get().getMemberId() != callerId) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED_WAITING);
        }
        waitingRepository.delete(id);
    }
}
