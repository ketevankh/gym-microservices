CREATE TABLE IF NOT EXISTS USERS (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     first_name VARCHAR(255) NOT NULL,
                                     last_name VARCHAR(255) NOT NULL,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     is_active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS TRAINING_TYPES (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              training_type VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS TRAINEES (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        address VARCHAR(255),
                                        date_of_birth DATE,
                                        user_id BIGINT NOT NULL,
                                        FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TRAINERS (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        training_type_id BIGINT NOT NULL,
                                        FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
                                        FOREIGN KEY (training_type_id) REFERENCES TRAINING_TYPES(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TRAININGS (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         trainee_id BIGINT NOT NULL,
                                         trainer_id BIGINT NOT NULL,
                                         training_type_id BIGINT NOT NULL,
                                         training_name VARCHAR(255) NOT NULL,
                                         training_date DATE NOT NULL,
                                         duration INT NOT NULL,
                                         FOREIGN KEY (trainee_id) REFERENCES TRAINEES(id) ON DELETE CASCADE,
                                         FOREIGN KEY (trainer_id) REFERENCES TRAINERS(id) ON DELETE CASCADE,
                                         FOREIGN KEY (training_type_id) REFERENCES TRAINING_TYPES(id) ON DELETE CASCADE
);
