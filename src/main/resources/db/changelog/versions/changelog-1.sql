--liquibase formatted sql

--changeset AntonKulakov:1
--comment first migration
CREATE TABLE Users
(
    id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    username varchar(100) NOT NULL UNIQUE,
    password varchar(255) NOT NULL
);

INSERT INTO Users (username, password)
VALUES ('test_user1', 'test_password'),
       ('test_user2', 'test_password');
--rollback truncate table demo;