package fu.quannt.reservation.dto;

public class ReservationDetailDTO extends ReservationDTO {
    private RoomDTO room;

    public RoomDTO getRoom() { return room; }
    public void setRoom(RoomDTO room) { this.room = room; }
}
