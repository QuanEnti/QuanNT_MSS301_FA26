package fu.quannt.user.dto;

import fu.quannt.user.common.OnCreate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDTOValidationTest {

    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    @Test
    void createGroupIncludesFormatAndRangeConstraints() {
        UserDTO dto = new UserDTO();
        dto.setUsername("creator_name_that_is_longer_than_thirty_chars");
        dto.setEmail("invalid-email");
        dto.setRole("UNKNOWN");
        dto.setRatePerPost(BigDecimal.ZERO);
        dto.setMaxDailyPosts(11);
        dto.setLevel(0);

        var violations = validator.validate(dto, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 6);
    }
}
