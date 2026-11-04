package com.mss301.watch.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class WatchRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsAllAssignmentRules() {
        assertThat(validator.validate(validRequest())).isEmpty();
    }

    @Test
    void rejectsShortNameLowPriceAndPastDate() {
        WatchRequest request = new WatchRequest(
                "Short",
                1,
                1,
                "Steel",
                "Leather",
                "100 meters",
                new BigDecimal("99.99"),
                LocalDate.now().minusDays(1),
                1
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("name", "price", "releaseDate");
    }

    private WatchRequest validRequest() {
        return new WatchRequest(
                "Valid Watch Name",
                1,
                1,
                "Steel",
                "Leather",
                "100 meters",
                new BigDecimal("100.00"),
                LocalDate.now(),
                1
        );
    }
}

