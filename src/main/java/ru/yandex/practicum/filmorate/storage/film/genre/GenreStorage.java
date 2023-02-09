package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface GenreStorage {

    SqlRowSet getGenre(int id);

    SqlRowSet findAllGenres();

    SqlRowSet getGenresOfFilmFromDB(int id);
}
