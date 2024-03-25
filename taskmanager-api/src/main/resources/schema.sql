CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL,
    CONSTRAINT email_unique UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    priority ENUM('LOW', 'MID', 'HIGH') NOT NULL,
    status ENUM('OPEN', 'FINISHED') NOT NULL,
    until_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);