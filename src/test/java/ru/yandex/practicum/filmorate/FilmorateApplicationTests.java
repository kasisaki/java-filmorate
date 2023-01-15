package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;

@SuppressWarnings("unused")
@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;

	@Test
	void contextLoads() {
	}

}
