package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.annotations.MinDate;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

public class FilmValidationTest {
    private static Validator validator;
    private static ValidatorFactory factory;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    public void shouldAcceptValidFilm() {
        Film film = createValidFilm();

        var violations = validator.validate(film);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void shouldRejectNullName() {
        Film film = createValidFilm();
        film.setName(null);

        assertViolation(film, "name", NotBlank.class, 1);
    }

    @Test
    public void shouldRejectEmptyName() {
        Film film = createValidFilm();
        film.setName("");

        assertViolation(film, "name", NotBlank.class, 1);
    }

    @Test
    public void shouldRejectBlankName() {
        Film film = createValidFilm();
        film.setName("     ");

        assertViolation(film, "name", NotBlank.class, 1);
    }

    @Test
    public void shouldRejectNullDescription() {
        Film film = createValidFilm();
        film.setDescription(null);

        assertViolation(film, "description", NotNull.class, 1);
    }

    @Test
    public void shouldRejectLongDescription() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        assertViolation(film, "description", Size.class, 1);
    }

    @Test
    public void shouldAcceptMaxDescription() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(200));

        var violations = validator.validate(film);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void shouldRejectNullReleaseDate() {
        Film film = createValidFilm();
        film.setReleaseDate(null);

        assertViolation(film, "releaseDate", NotNull.class, 1);
    }

    @Test
    public void shouldRejectInvalidDate() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1894, 1, 1));

        assertViolation(film, "releaseDate", MinDate.class, 1);
    }

    @Test
    public void shouldAcceptMinDate() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        var violations = validator.validate(film);

        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void shouldRejectNullDuration() {
        Film film = createValidFilm();
        film.setDuration(null);

        assertViolation(film, "duration", NotNull.class, 1);
    }

    @Test
    public void shouldRejectZeroDuration() {
        Film film = createValidFilm();
        film.setDuration(0);

        assertViolation(film, "duration", Positive.class, 1);
    }


    @Test
    public void shouldRejectNegativeDuration() {
        Film film = createValidFilm();
        film.setDuration(-1);

        assertViolation(film, "duration", Positive.class, 1);
    }


    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }

    private void assertViolation(Film film, String fieldName,
                                 Class<? extends Annotation> annotation, int expectedCount) {
        var violations = validator.validate(film);

        Assertions.assertEquals(expectedCount, violations.size());
        Assertions.assertTrue(violations.stream()
                .anyMatch(v ->
                        v.getPropertyPath().toString().equals(fieldName)
                                && v.getConstraintDescriptor()
                                .getAnnotation()
                                .annotationType()
                                .equals(annotation)));
    }
}
