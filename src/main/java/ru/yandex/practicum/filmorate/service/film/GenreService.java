package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        List<Genre> GenreSet = new ArrayList<>();
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


    public Set<Genre> getGenresOfFilm(Integer filmId) {
        SqlRowSet urs = genreStorage.getGenresOfFilmFromDB(filmId);

        Map<Integer, String> genresMap = new HashMap<>();

        while (urs.next()) {
            genresMap.put(urs.getInt("GENRE_ID"), urs.getString("name"));
        }
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        for (Integer genreID : genresMap.keySet()) {
            genres.add(Genre.builder().id(genreID).name(genresMap.get(genreID)).build());
        }
        return genres;
    }

    private  Genre buildGenre(SqlRowSet urs) {
        return Genre.builder()
                .id(urs.getInt("genre_id"))
                .name(urs.getString("name"))
                .build();
    }
}
