CREATE TABLE IF NOT EXISTS auth_user(
    id varchar(128) NOT NULL PRIMARY KEY,
    email varchar(319),
    hashed_password varchar(60)
);
