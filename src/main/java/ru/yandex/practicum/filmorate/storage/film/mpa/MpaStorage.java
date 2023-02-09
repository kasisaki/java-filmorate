package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface MpaStorage {
    SqlRowSet findAllMpa();

    SqlRowSet findMpa(int mpaId);
}
