package fu.quannt.room.dto;

import fu.quannt.room.common.OnCreate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RoomDTOValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createRequiresAllDatabaseMandatoryFields() {
        assertThat(validator.validate(new RoomDTO(), OnCreate.class))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("roomNumber", "roomType", "pricePerNight", "capacity", "floor");

        RoomDTO invalid = new RoomDTO();
        invalid.setRoomNumber("   ");
        invalid.setPricePerNight(BigDecimal.ZERO);
        invalid.setCapacity(11);
        invalid.setFloor(0);
        assertThat(validator.validate(invalid, OnCreate.class))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("roomNumber", "pricePerNight", "capacity", "floor");
    }
}
