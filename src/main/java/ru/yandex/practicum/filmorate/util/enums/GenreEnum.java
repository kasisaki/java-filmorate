package ru.yandex.practicum.filmorate.util.enums;

public enum GenreEnum {
    ACTION( "Боевик"),
    CARTOON("Мультфильм"),
    COMEDY("Комедия"),
    DOCUMENTARY("Документальный"),
    DRAMA("Драма"),
    THRILLER("Триллер");

    private final String value;

    GenreEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
