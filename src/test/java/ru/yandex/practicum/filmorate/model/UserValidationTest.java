package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

public class UserValidationTest {
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
    public void shouldAcceptValidUser() {
        User user = createValidUser();

        var violations = validator.validate(user);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldRejectNullEmail() {
        User user = createValidUser();
        user.setEmail(null);

        assertViolation(user, "email", NotBlank.class, 1);
    }

    @Test
    public void shouldRejectEmptyEmail() {
        User user = createValidUser();
        user.setEmail("");

        assertViolation(user, "email", NotBlank.class, 1);
    }

    @Test
    public void shouldRejectBlankEmail() {
        User user = createValidUser();
        user.setEmail("   ");

        assertViolation(user, "email", NotBlank.class, 2);
    }

    @Test
    public void shouldRejectInvalidEmail() {
        User user = createValidUser();
        user.setEmail("super1-@-@email.com");

        assertViolation(user, "email", Email.class, 1);
    }

    @Test
    public void shouldRejectNullLogin() {
        User user = createValidUser();
        user.setLogin(null);

        assertViolation(user, "login", NotBlank.class, 1);
    }

    @Test
    public void shouldRejectEmptyLogin() {
        User user = createValidUser();
        user.setLogin("");

        assertViolation(user, "login", NotBlank.class, 2);
    }

    @Test
    public void shouldRejectBlankLogin() {
        User user = createValidUser();
        user.setLogin("    ");

        assertViolation(user, "login", NotBlank.class, 2);
    }

    @Test
    public void shouldRejectInvalidLogin() {
        User user = createValidUser();
        user.setLogin("weq qw");

        assertViolation(user, "login", Pattern.class, 1);
    }

    @Test
    public void shouldAcceptNullName() {
        User user = createValidUser();
        user.setName(null);

        user.setDefaultName();

        Assertions.assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void shouldAcceptEmptyName() {
        User user = createValidUser();
        user.setName("");

        user.setDefaultName();

        Assertions.assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void shouldAcceptBlankName() {
        User user = createValidUser();
        user.setName("    ");

        user.setDefaultName();

        Assertions.assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void shouldRejectNullBirthday() {
        User user = createValidUser();
        user.setBirthday(null);

        assertViolation(user, "birthday", NotNull.class, 1);
    }

    @Test
    public void shouldRejectFutureBirthday() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertViolation(user, "birthday", PastOrPresent.class, 1);
    }

    @Test
    public void shouldAcceptNowBirthday() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now());

        var violations = validator.validate(user);

        Assertions.assertEquals(0, violations.size());
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    private void assertViolation(User user, String fieldName,
                                 Class<? extends Annotation> annotation, int expectedCount) {
        var violations = validator.validate(user);

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
