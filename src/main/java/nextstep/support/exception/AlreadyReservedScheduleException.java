package nextstep.support.exception;

public class AlreadyReservedScheduleException extends RuntimeException {
    public AlreadyReservedScheduleException() {
    }

    public AlreadyReservedScheduleException(String message) {
        super(message);
    }
}