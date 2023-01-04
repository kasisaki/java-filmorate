package ru.yandex.practicum.filmorate.util;

public class IdGenerator {
    private static long id = 0;

    public static long generate() {
        return ++id;
    }
}
