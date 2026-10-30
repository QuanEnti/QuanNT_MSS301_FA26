package fu.quannt.reservation.dto;

public class RoomApiResponseDTO {
    private int status;
    private RoomDTO data;
    private String timestamp;
    private String message;

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public RoomDTO getData() { return data; }
    public void setData(RoomDTO data) { this.data = data; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
