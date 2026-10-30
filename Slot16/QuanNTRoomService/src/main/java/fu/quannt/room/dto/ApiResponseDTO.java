package fu.quannt.room.dto;

import java.time.Instant;

public class ApiResponseDTO {
    private int status;
    private Object data;
    private String timestamp;
    private String message;

    public ApiResponseDTO() {
    }

    public ApiResponseDTO(int status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.timestamp = Instant.now().toString();
        this.message = message;
    }

    public static ApiResponseDTO success(String message, Object data) {
        return new ApiResponseDTO(1, data, message);
    }

    public static ApiResponseDTO error(int status, String message) {
        return new ApiResponseDTO(status, null, message);
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
