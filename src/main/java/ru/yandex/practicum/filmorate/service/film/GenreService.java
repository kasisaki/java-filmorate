package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.dbException.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final JdbcTemplate jdbcTemplate;



    public List<Genre> findAll() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build()));
    }

    public Genre findGenre(int id) {
        List<Genre> genres =  jdbcTemplate.query("SELECT * FROM GENRES WHERE genre_id =" + id , ((urs, rowNum) -> Genre.builder()
                .id(urs.getInt("genre_id"))
                .name(urs.getString("name"))
                .build()));
        if (genres.size() == 0) {
            throw new GenreNotFoundException(String.format("Genre with id %d not found", id));
        }
        return genres.get(0);
    }
}
