package fu.quannt.post.dto;

import fu.quannt.post.common.OnCreate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostDTOValidationTest {

    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    @Test
    void createGroupIncludesFormatAndRangeConstraints() {
        PostDTO dto = new PostDTO();
        dto.setAdvertiserName("Shop ABC");
        dto.setAdvertiserEmail("invalid-email");
        dto.setAdvertiserPhone("090000000000000000000");
        dto.setStartDate(Date.valueOf("2026-07-25"));
        dto.setEndDate(Date.valueOf("2026-07-28"));
        dto.setNumberOfPosts(0);
        dto.setUserId(1L);

        var violations = validator.validate(dto, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3);
    }
}
