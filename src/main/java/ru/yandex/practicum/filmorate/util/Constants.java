package ru.yandex.practicum.filmorate.util;

public class Constants {

    public final static String DATE_LIMIT = "1895-12-27";
    /* Изначально я так и хотел. Однако эта константа у меня используется для валидации и система
    не дала мне использовать тип ни LocaDate, ни DateTime, ни ZonedDateTime для валидации.
    Точное сообщение не помню, но если нужно могу повторить и привести это сообщение...
    т.к. константа с типом LocalDate мне пока не нужна для других целей я ее пока убрал вообще.
     */

    public final static int DESCRIPTION_MAX_LENGTH = 200;

    public final static int FILM_COUNT_DEFAULT = 10;
}
