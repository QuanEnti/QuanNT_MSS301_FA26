package fu.quannt.room.dto;

import fu.quannt.room.common.OnCreate;
import fu.quannt.room.entity.RoomStatus;
import fu.quannt.room.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RoomDTO {
    private Long roomId;

    @NotBlank(groups = OnCreate.class, message = "roomNumber is required")
    @Size(max = 20, message = "roomNumber must not exceed 20 characters")
    private String roomNumber;

    @NotNull(groups = OnCreate.class, message = "roomType is required")
    private RoomType roomType;

    @NotNull(groups = OnCreate.class, message = "pricePerNight is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "pricePerNight must be greater than 0")
    private BigDecimal pricePerNight;

    @NotNull(groups = OnCreate.class, message = "capacity is required")
    @Min(value = 1, message = "capacity must be between 1 and 10")
    @Max(value = 10, message = "capacity must be between 1 and 10")
    private Integer capacity;

    @NotNull(groups = OnCreate.class, message = "floor is required")
    @Min(value = 1, message = "floor must be greater than or equal to 1")
    private Integer floor;

    private RoomStatus status;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
}
