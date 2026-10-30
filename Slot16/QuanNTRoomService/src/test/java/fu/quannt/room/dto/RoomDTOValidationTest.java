package fu.quannt.room.dto;

import fu.quannt.room.common.OnCreate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoomDTOValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createRequiresAllDatabaseMandatoryFields() {
        assertThat(validator.validate(new RoomDTO(), OnCreate.class))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("roomNumber", "roomType", "pricePerNight", "capacity", "floor");
    }
}
