package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Set<Genre> findAllGenres() {
        Set<Genre> GenreSet = new HashSet<>();
        SqlRowSet urs = genreStorage.findAllGenres();

        while (urs.next()) {
            GenreSet.add(buildGenre(urs));
        }
        return GenreSet;
    }

    public Genre findGenre(int id) {
        SqlRowSet urs = genreStorage.findGenre(id);
        if (urs.next()) {
            return buildGenre(urs);
        }
        throw new ElementNotFoundException("Genre with id " + id + " not found");
    }

    private  Genre buildGenre(SqlRowSet urs) {
        return Genre.builder()
                .id(urs.getInt("genre_id"))
                .name(urs.getString("name"))
                .build();
    }
}
