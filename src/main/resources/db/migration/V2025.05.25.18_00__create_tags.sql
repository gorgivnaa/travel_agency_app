CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY(id)
);

INSERT INTO tags (name)
VALUES ('Нужна виза'),
        ('Пляжный отдых'),
        ('Горнолыжный курорт'),
        ('Гастрономия'),
        ('Море'),
        ('Вино'),
        ('Семейный'),
        ('Экскурсия'),
        ('Люкс'),
        ('Трекинг'),
        ('Релакс'),
        ('Все включено'),
        ('Не нужна виза');