# java-filmorate

### Таблица
![ER-диаграмма](https://github.com/user-attachments/assets/e2d46323-a8dd-4cdf-bdf2-1ec016316e0b)

1. users: информация о пользователях
2. friends: дружественные связи между пользователями
3. likes: отметки "мне нравится" пользователей
4. films: информация о фильмах
5. rating_mpaa: возрастной рейтинг фильмов по системе MPAA
6. film_genre: связь между жанром и фильмом
7. genre: жанры фильмов

### Примеры запросов
1. Получить всех пользователей
```
SELECT *
FROM users;
```
2. Получить все фильмы
```
SELECT *
FROM films AS f
LEFT JOIN rating_mpaa AS r ON f.rating_id = r.rating_id
LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id
LEFT JOIN genre AS g ON fg.genre_id = g.genre_id; 
```
