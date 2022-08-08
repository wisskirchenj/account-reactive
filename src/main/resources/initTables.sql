DROP TABLE IF EXISTS SALARY;
DROP TABLE IF EXISTS LOGIN;
CREATE TABLE IF NOT EXISTS LOGIN (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      name VARCHAR (64),
                      lastname VARCHAR (64),
                      email VARCHAR_IGNORECASE (64) UNIQUE NOT NULL,
                      password VARCHAR (128) NOT NULL
);
CREATE TABLE IF NOT EXISTS SALARY (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      email VARCHAR_IGNORECASE (64) NOT NULL,
                      FOREIGN KEY(email) REFERENCES LOGIN(email),
                      period VARCHAR (7) NOT NULL,
                      salary BIGINT NOT NULL
);
