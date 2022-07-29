CREATE TABLE IF NOT EXISTS LOGIN (
                      id LONG PRIMARY KEY NOT NULL,
                      name VARCHAR (50),
                      lastname VARCHAR (50),
                      email VARCHAR (50) NOT NULL,
                      password VARCHAR (128) NOT NULL
);
INSERT INTO LOGIN VALUES (1, 'Jürgen', 'Wißkirchen', 'juergen.wisskirchen@cofinpro.de', '{bcrypt}$2y$05$TlPiUDybwL.CST9U8x/7TOwwx4HEknYLhE2cYQCQFjZwZUYl3I2nO' );
INSERT INTO LOGIN VALUES (2, 'Frank', 'Hase', 'f.h@ab.de', '{noop}secret' );