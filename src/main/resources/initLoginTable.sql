DROP TABLE IF EXISTS LOGIN;
CREATE TABLE IF NOT EXISTS LOGIN (
                      id BIGINT PRIMARY KEY NOT NULL,
                      name VARCHAR (50),
                      lastname VARCHAR (50),
                      email VARCHAR (50) NOT NULL,
                      password VARCHAR (128) NOT NULL
);
