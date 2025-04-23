-- Таблица "countries" (страны)
CREATE TABLE countries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    image LONGBLOB NOT NULL
);

-- Таблица "hotels" (отели)
CREATE TABLE hotels (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  rating FLOAT,
  description TEXT,
  country_id BIGINT,
  FOREIGN KEY (country_id) REFERENCES countries (id)
);

-- Таблица "tours" (туры)
CREATE TABLE tours (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(100) not null,
price DECIMAL(10,2) not null,
description TEXT,
place_quantity INT NOT NULL DEFAULT 10,
check_in_date DATE DEFAULT (CURRENT_DATE),
check_out_date DATE  DEFAULT (CURRENT_DATE),
country_id BIGINT,
hotel_id BIGINT,
foreign key (hotel_id) references hotels (id),
foreign key (country_id) references countries (id)
);

-- Таблица "users" (клиенты)
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  phone VARCHAR(20) NOT NULL UNIQUE,
  password VARCHAR(256) NOT NULL,
  is_admin BOOLEAN DEFAULT FALSE
);

-- Таблица "additional_services" (дополнительные сервисы)
CREATE TABLE additional_services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT
);

-- Таблица "orders" (заявки)
CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  place_quantity INT NOT NULL DEFAULT 10,
  order_date DATETIME NOT NULL,
  user_id BIGINT,
  tour_id BIGINT,
  service_id BIGINT,
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (tour_id) REFERENCES tours (id),
  FOREIGN KEY (service_id) REFERENCES additional_services (id)
);

-- Таблица "bookings" (бронирования)
CREATE TABLE bookings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  price DECIMAL(8,2) NOT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  place_quantity INT NOT NULL DEFAULT 1,
  user_id BIGINT,
  tour_id BIGINT,
  hotel_id BIGINT,
  service_id BIGINT,
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (hotel_id) REFERENCES hotels (id),
  FOREIGN KEY (tour_id) REFERENCES tours (id),
  FOREIGN KEY (service_id) REFERENCES additional_services (id)
);