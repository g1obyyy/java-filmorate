package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
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
    public Film create(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на создание фильма");

        newFilm.setId(nextId.incrementAndGet());
        films.put(newFilm.getId(), newFilm);

        log.info("Фильм успешно создан: id={}, name={}",
                newFilm.getId(), newFilm.getName());
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма");

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
}
