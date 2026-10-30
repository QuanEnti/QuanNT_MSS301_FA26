package fu.quannt.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fu.quannt.reservation.common.OnCreate;
import fu.quannt.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

public class ReservationDTO {
    private Long reservationId;

    @NotBlank(groups = OnCreate.class, message = "guestName is required")
    @Pattern(regexp = ".*\\S.*", message = "guestName must not be blank")
    @Size(max = 100, message = "guestName must not exceed 100 characters")
    private String guestName;

    @NotBlank(groups = OnCreate.class, message = "guestEmail is required")
    @Pattern(regexp = ".*\\S.*", message = "guestEmail must not be blank")
    @Email(message = "guestEmail must be valid")
    @Size(max = 100, message = "guestEmail must not exceed 100 characters")
    private String guestEmail;

    @NotBlank(groups = OnCreate.class, message = "guestPhone is required")
    @Pattern(regexp = ".*\\S.*", message = "guestPhone must not be blank")
    @Size(max = 20, message = "guestPhone must not exceed 20 characters")
    private String guestPhone;

    @NotNull(groups = OnCreate.class, message = "checkInDate is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date checkInDate;

    @NotNull(groups = OnCreate.class, message = "checkOutDate is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date checkOutDate;

    @NotNull(groups = OnCreate.class, message = "numberOfGuests is required")
    @Min(value = 1, message = "numberOfGuests must be between 1 and 10")
    @Max(value = 10, message = "numberOfGuests must be between 1 and 10")
    private Integer numberOfGuests;

    private BigDecimal totalAmount;
    private ReservationStatus status;

    @NotNull(groups = OnCreate.class, message = "roomId is required")
    private Long roomId;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}
