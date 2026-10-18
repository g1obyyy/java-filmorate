package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(MinDate annotation) {
        minDate = LocalDate.parse(annotation.value());
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(minDate);
    }
}
