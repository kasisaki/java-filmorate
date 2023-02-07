package ru.yandex.practicum.filmorate.storage.film.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage {

    JdbcTemplate jdbcTemplate;

    public SqlRowSet findAll() {
        return jdbcTemplate.queryForRowSet("SELECT * FROM GENRES");
    }

    public SqlRowSet findGenre(int id) {
        return jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE genre_id =?", id);
    }

//    public Genre[] getGenresOfFilmFromDB(int FilmId) {
//        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT fg.GENRE_ID, name FROM FILM_GENRES as fg " +
//                "LEFT JOIN GENRES g on fg.GENRE_ID = g.GENRE_ID " +
//                "WHERE FILM_ID =?", FilmId);
//
//        Map<Integer, String> genresMap = new HashMap<>();
//
//        while (rs.next()) {
//            genresMap.put(rs.getInt("GENRE_ID"), rs.getString("name"));
//        }
//        Genre[] genres = new Genre[genresMap.size()];
//        int i = 0;
//        for (Integer genreID : genresMap.keySet()) {
//            genres[i] = Genre.builder().id(genreID).name(genresMap.get(genreID)).build();
//            log.warn(genres[i].toString());
//            i++;
//        }
//        return genres;
//    }
}
