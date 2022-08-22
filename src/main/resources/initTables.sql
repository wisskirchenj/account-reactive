DROP TABLE IF EXISTS SALARY;
DROP TABLE IF EXISTS LOGIN_ROLES;
DROP TABLE IF EXISTS LOGIN;
DROP TABLE IF EXISTS ROLES;
DROP TABLE IF EXISTS AUDIT;
CREATE TABLE IF NOT EXISTS LOGIN (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      name VARCHAR (64),
                      lastname VARCHAR (64),
                      email VARCHAR_IGNORECASE (64) UNIQUE NOT NULL,
                      password VARCHAR (128) NOT NULL,
                      account_locked BOOL NOT NULL,
                      failed_logins SMALLINT
);
CREATE TABLE IF NOT EXISTS SALARY (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      email VARCHAR_IGNORECASE (64) NOT NULL,
                      FOREIGN KEY(email) REFERENCES LOGIN(email),
                      period VARCHAR (7) NOT NULL,
                      salary BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS ROLES (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      user_role VARCHAR_IGNORECASE (20) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS LOGIN_ROLES (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      email VARCHAR_IGNORECASE (64) NOT NULL,
                      FOREIGN KEY(email) REFERENCES LOGIN(email),
                      user_role VARCHAR_IGNORECASE (20) NOT NULL,
                      FOREIGN KEY(user_role) REFERENCES ROLES(user_role)
);
CREATE TABLE IF NOT EXISTS AUDIT (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                      occurred_on DATE NOT NULL,
                      action VARCHAR_IGNORECASE (20) NOT NULL,
                      subject VARCHAR_IGNORECASE (64),
                      object VARCHAR (128),
                      path VARCHAR (64) NOT NULL
);