package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        List<Mpa> mpaList = new ArrayList<>();
        SqlRowSet urs = mpaStorage.findAllMpa(); //or false

        while (urs.next()) {
            mpaList.add(buildMpa(urs));
        }
        return mpaList;
    }

    public Mpa getMpa(int id) {
        SqlRowSet urs = mpaStorage.findMpa(id);
        if (urs.next()) {
            return buildMpa(urs);
        }
        throw new ElementNotFoundException("Mpa with id " + id + " not found");
    }

    private  Mpa buildMpa(SqlRowSet urs) {
        return Mpa.builder()
              .id(urs.getInt("mpa_id"))
              .name(urs.getString("name"))
              .build();
    }
}
