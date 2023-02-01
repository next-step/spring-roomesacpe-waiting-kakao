package roomwaiting.nextstep.reservation;

public enum ReservationStatus {
    NOT_APPROVED("Reservation Not Approved"),
    APPROVED("Reservation Approved"),
    CANCEL("Reservation Cancel"),
    WAITING_CANCEL("Reservation Cancel Waiting"),
    DECLINE("Reservation Declined"),
    ;


    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
