-- Drop all tables and sequences if they exist

DROP TABLE IF EXISTS user_liked_ratings CASCADE;
DROP TABLE IF EXISTS user_favorites CASCADE;
DROP TABLE IF EXISTS media_entries_genres CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS media_entries CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS media_types CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP SEQUENCE IF EXISTS users_id_seq CASCADE;
DROP SEQUENCE IF EXISTS media_entries_id_seq CASCADE;
DROP SEQUENCE IF EXISTS ratings_id_seq CASCADE;
DROP SEQUENCE IF EXISTS genres_id_seq CASCADE;
DROP SEQUENCE IF EXISTS media_types_id_seq CASCADE;

-- media_types table (movie, series, game)

CREATE TABLE media_types (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO media_types (name) VALUES
                                   ('movie'),
                                   ('series'),
                                   ('game');

-- genres table

CREATE TABLE genres (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL UNIQUE
);

-- users table

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       favorite_genre TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- media_entries table

CREATE TABLE media_entries (
                               id SERIAL PRIMARY KEY,
                               created_by BIGINT NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               description TEXT,
                               media_type INT NOT NULL,  -- FK to media_types
                               release_year INT CHECK (release_year >= 1800 AND release_year <= EXTRACT(YEAR FROM CURRENT_DATE) + 1),
                               age_restriction INT CHECK (age_restriction >= 0),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_media_created_by
                                   FOREIGN KEY (created_by)
                                       REFERENCES users (id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_media_type
                                   FOREIGN KEY (media_type)
                                       REFERENCES media_types(id)
                                       ON DELETE RESTRICT
);

-- media_entries_genres

CREATE TABLE media_entries_genres (
                                      media_entry_id INT NOT NULL,
                                      genre_id INT NOT NULL,
                                      PRIMARY KEY (media_entry_id, genre_id),

                                      CONSTRAINT fk_meg_media
                                          FOREIGN KEY (media_entry_id)
                                              REFERENCES media_entries (id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_meg_genre
                                          FOREIGN KEY (genre_id)
                                              REFERENCES genres (id)
                                              ON DELETE CASCADE
);

-- ratings table

CREATE TABLE ratings (
                         id SERIAL PRIMARY KEY,
                         created_by BIGINT NOT NULL,
                         media_entry_id INT,
                         is_confirmed BOOLEAN DEFAULT FALSE,
                         comment TEXT,
                         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         star_value INT NOT NULL CHECK (star_value BETWEEN 1 AND 5),

                         CONSTRAINT fk_rating_user
                             FOREIGN KEY (created_by)
                                 REFERENCES users (id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_rating_media
                             FOREIGN KEY (media_entry_id)
                                 REFERENCES media_entries (id)
                                 ON DELETE CASCADE
);

-- user_favorites

CREATE TABLE user_favorites (
                                user_id BIGINT NOT NULL,
                                media_entry_id INT NOT NULL,
                                PRIMARY KEY (user_id, media_entry_id),

                                CONSTRAINT fk_fav_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users (id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_fav_media
                                    FOREIGN KEY (media_entry_id)
                                        REFERENCES media_entries (id)
                                        ON DELETE CASCADE
);

-- user_liked_ratings

CREATE TABLE user_liked_ratings (
                                    user_id BIGINT NOT NULL,
                                    rating_id INT NOT NULL,
                                    PRIMARY KEY (user_id, rating_id),

                                    CONSTRAINT fk_liked_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES users (id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_liked_rating
                                        FOREIGN KEY (rating_id)
                                            REFERENCES ratings (id)
                                            ON DELETE CASCADE
);
