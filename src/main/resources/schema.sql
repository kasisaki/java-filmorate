DROP ALL OBJECTS DELETE FILES;

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id int2 PRIMARY KEY AUTO_INCREMENT,
    name   varchar(80)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id int2 PRIMARY KEY AUTO_INCREMENT,
    name     varchar(20)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_id       int2 REFERENCES mpa (mpa_id),
    genre_id     int2 REFERENCES genres (genre_id),
    name         varchar(250),
    description  varchar(200),
    release_date date,
    duration     int2 CHECK (duration >= 0)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  int PRIMARY KEY AUTO_INCREMENT,
    login    varchar(20) UNIQUE  NOT NULL,
    name     varchar(50),
    email    varchar(320) UNIQUE NOT NULL,
    birthday date
);

CREATE TABLE IF NOT EXISTS friendships
(
    friendship_id int PRIMARY KEY AUTO_INCREMENT,
    from_user     int REFERENCES users (user_id) ON DELETE CASCADE,
    to_user       int REFERENCES users (user_id) ON DELETE CASCADE,
    accepted      boolean DEFAULT false
);

CREATE TABLE IF NOT EXISTS likes
(
    like_id int PRIMARY KEY AUTO_INCREMENT,
    film_id int REFERENCES films (film_id) ON DELETE CASCADE,
    user_id int REFERENCES users (user_id) ON DELETE CASCADE
);