-- Active: 1765391957085@@127.0.0.1@3306@biblioteca
CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

CREATE TABLE IF NOT EXISTS Libro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR (200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    ISBN VARCHAR(50) UNIQUE,
    editorial VARCHAR(50),
    paginas INT,
    sinopsis TEXT,
    inicio_lectura DATE,
    fin_lectura DATE,
    estado ENUM ("lo tengo", "leyendo", "leido", "deseado") NOT NULL DEFAULT "lo tengo"
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
COMMIT;
