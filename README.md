# java-filmorate

### Таблица
![ER-диаграмма](https://github.com/user-attachments/assets/f9aaa659-28ac-47f9-bc21-0e6f72bddd25)

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
LEFT JOIN rating AS r ON f.rating_id = r.rating_id
LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id
LEFT JOIN genre AS g ON fg.genre_id = g.genre_id; 
```
