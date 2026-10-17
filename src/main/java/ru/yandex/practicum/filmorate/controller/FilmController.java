package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(0);

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен запрос на получение всех фильмов");

        Collection<Film> result = new ArrayList<>(films.values());

        log.debug("Найдено фильмов {}", result.size());
        return result;
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        log.info("Получен запрос на создание фильма");
        validateFilmObj(newFilm);

        newFilm.setId(nextId.incrementAndGet());
        films.put(newFilm.getId(), newFilm);

        log.info("Фильм успешно создан: id={}, name={}",
                newFilm.getId(), newFilm.getName());
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма");

        validateFilmObj(newFilm);
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = films.replace(newFilm.getId(), newFilm);
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        log.info("Фильм успешно обновлен: id={}, name={}",
                newFilm.getId(), newFilm.getName());
        return newFilm;
    }


    private void validateFilmObj(Film film) {
        if (film == null) {
            throw new ValidationException(
                    "Фильм должен быть инициализирован");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException(
                    "Название фильма должно быть инициализировано и не пустым");
        }

        if (film.getDescription() == null || film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException(
                    "Длина описания фильма должна быть инициализирована и не должна превышать 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(
                    "дата релиза фильма должна быть инициализирована и не раньше: " + MIN_RELEASE_DATE);
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException(
                    "Продолжительность фильма должна быть инициализирована и являться положительным числом.");
        }
    }
}
