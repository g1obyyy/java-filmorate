package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(0);

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос на получение всех пользователей");

        Collection<User> result = new ArrayList<>(users.values());

        log.debug("Найдено пользователей: {}", result.size());
        return result;
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        log.info("Получен запрос на создание пользователя");
        validateUserObj(newUser);

        newUser.setId(nextId.incrementAndGet());
        users.put(newUser.getId(), newUser);

        log.info("Пользователь успешно создан: id={}, login={}",
                newUser.getId(), newUser.getLogin());
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя");

        validateUserObj(newUser);
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = users.replace(newUser.getId(), newUser);
        if (oldUser == null) {
            throw new NotFoundException("Пользователь id = " + newUser.getId() + " не найден");
        }

        log.info("Пользователь успешно обновлен: id={}, login={}",
                newUser.getId(), newUser.getLogin());
        return newUser;
    }

    private void validateUserObj(User user) {
        if (user == null) {
            throw new ValidationException("Пользователь должен быть инициализирован");
        }

        if (user.getEmail() == null
        || user.getEmail().isBlank()
        || !user.getEmail().contains("@")
        || user.getEmail().indexOf("@") != user.getEmail().lastIndexOf("@")) {
            throw new ValidationException(
                    "Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null
        || user.getLogin().isBlank()
        || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null
        || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(
                    "Дата рождения должна быть инициализирована и не может быть в будущем");
        }
    }
}
