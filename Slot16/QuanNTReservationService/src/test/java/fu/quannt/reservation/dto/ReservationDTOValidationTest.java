package fu.quannt.reservation.dto;

import fu.quannt.reservation.common.OnCreate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDTOValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createRequiresReservationDatabaseFields() {
        assertThat(validator.validate(new ReservationDTO(), OnCreate.class))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("guestName", "guestEmail", "guestPhone", "checkInDate",
                        "checkOutDate", "numberOfGuests", "roomId");

        ReservationDTO invalid = new ReservationDTO();
        invalid.setGuestName("   ");
        invalid.setGuestEmail("not-an-email");
        invalid.setGuestPhone("   ");
        invalid.setNumberOfGuests(11);
        assertThat(validator.validate(invalid, OnCreate.class))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("guestName", "guestEmail", "guestPhone", "numberOfGuests");
    }
}
