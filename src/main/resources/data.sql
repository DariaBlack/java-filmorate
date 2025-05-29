DELETE FROM likes;
DELETE FROM film_genre;
DELETE FROM films WHERE rating_id IN (SELECT rating_id FROM rating_mpaa);
DELETE FROM rating_mpaa;
DELETE FROM genre;

INSERT INTO rating_mpaa (rating_id, name) VALUES
                                              (1, 'G'),
                                              (2, 'PG'),
                                              (3, 'PG-13'),
                                              (4, 'R'),
                                              (5, 'NC-17');
INSERT INTO genre (genre_id, name) VALUES
                                       (1, 'Комедия'),
                                       (2, 'Драма'),
                                       (3, 'Мультфильм'),
                                       (4, 'Триллер'),
                                       (5, 'Документальный'),
                                       (6, 'Боевик');