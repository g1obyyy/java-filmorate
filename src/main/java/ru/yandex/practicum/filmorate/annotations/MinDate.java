package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinDate {

    String value();

    String message() default "Дата не может быть раньше {}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
