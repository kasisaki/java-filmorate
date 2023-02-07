package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface GenreStorage {

    SqlRowSet findGenre(int id);

    SqlRowSet findAllGenres();

    //Genre[] getGenresOfFilmFromDB(int id);
}
