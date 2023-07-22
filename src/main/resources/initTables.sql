DROP TABLE IF EXISTS ROLES;
CREATE TABLE IF NOT EXISTS LOGIN (
                      id BIGSERIAL PRIMARY KEY NOT NULL,
                      name VARCHAR (64),
                      lastname VARCHAR (64),
                      email VARCHAR (64) UNIQUE NOT NULL,
                      password VARCHAR (128) NOT NULL,
                      account_locked BOOL NOT NULL,
                      failed_logins SMALLINT
);
CREATE TABLE IF NOT EXISTS SALARY (
                      id BIGSERIAL PRIMARY KEY NOT NULL,
                      email VARCHAR (64) NOT NULL,
                      period VARCHAR (7) NOT NULL,
                      salary BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS ROLES (
                      id BIGSERIAL PRIMARY KEY NOT NULL,
                      user_role VARCHAR (20) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS LOGIN_ROLES (
                      id BIGSERIAL PRIMARY KEY NOT NULL,
                      email VARCHAR (64) NOT NULL,
                      user_role VARCHAR (20) NOT NULL
);
CREATE TABLE IF NOT EXISTS AUDIT (
                      id BIGSERIAL PRIMARY KEY NOT NULL,
                      date DATE NOT NULL,
                      action VARCHAR (20) NOT NULL,
                      subject VARCHAR (64),
                      object VARCHAR (128),
                      path VARCHAR (64) NOT NULL
);