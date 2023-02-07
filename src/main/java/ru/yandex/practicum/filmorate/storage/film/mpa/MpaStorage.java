package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface MpaStorage {
    public SqlRowSet getAllMpa();

    public SqlRowSet getMpa(int mpaId);
}
