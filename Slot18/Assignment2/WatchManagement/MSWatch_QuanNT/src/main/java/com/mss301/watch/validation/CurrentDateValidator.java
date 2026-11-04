package com.mss301.watch.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.ZoneId;

public class CurrentDateValidator implements ConstraintValidator<CurrentDate, LocalDate> {

    private static final ZoneId ASSIGNMENT_TIME_ZONE = ZoneId.of("Asia/Bangkok");

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || value.equals(LocalDate.now(ASSIGNMENT_TIME_ZONE));
    }
}

