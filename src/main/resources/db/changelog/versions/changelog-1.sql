--liquibase formatted sql

--changeset AntonKulakov:1
--comment first migration
CREATE TABLE Users
(
    id int PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    username varchar(100) NOT NULL UNIQUE,
    password varchar(255) NOT NULL
);
--rollback truncate table demo;