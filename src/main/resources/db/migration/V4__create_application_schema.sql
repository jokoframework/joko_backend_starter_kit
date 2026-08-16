CREATE SCHEMA IF NOT EXISTS "basic";
CREATE SCHEMA IF NOT EXISTS "profile";

CREATE SEQUENCE IF NOT EXISTS "basic".person_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "basic".address_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "basic".notification_id_seq START WITH 7 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "profile".user_id_seq START WITH 2 INCREMENT BY 1;

CREATE TABLE "basic".country (
    id VARCHAR(10) PRIMARY KEY,
    description VARCHAR(50)
);

CREATE TABLE "basic".person (
    id BIGINT PRIMARY KEY,
    name VARCHAR(250),
    lastname VARCHAR(255),
    identification_number VARCHAR(100) NOT NULL,
    birthdate TIMESTAMP,
    sex VARCHAR(100),
    marital_status VARCHAR(100),
    mobile_phone VARCHAR(100),
    email VARCHAR(255),
    nationality VARCHAR(100),
    CONSTRAINT users_identifnumber_uniq UNIQUE (identification_number)
);

CREATE TABLE "profile"."USER" (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile VARCHAR(50) NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_access_date TIMESTAMP,
    person_id BIGINT,
    CONSTRAINT users_username_uniq UNIQUE (username),
    CONSTRAINT fk_user_person_id FOREIGN KEY (person_id) REFERENCES "basic".person(id)
);

CREATE TABLE "basic".address (
    id BIGINT PRIMARY KEY,
    address VARCHAR(255),
    neighborhood VARCHAR(100),
    city VARCHAR(100),
    country_code VARCHAR(3),
    type VARCHAR(20),
    person_id BIGINT,
    CONSTRAINT fk_address_person_id FOREIGN KEY (person_id) REFERENCES "basic".person(id)
);

CREATE TABLE "basic".notification (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    read_date TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) DEFAULT 'info' NOT NULL,
    channel VARCHAR(50) DEFAULT 'system' NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES "profile"."USER"(id)
);
