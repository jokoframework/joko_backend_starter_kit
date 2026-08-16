CREATE SEQUENCE IF NOT EXISTS "joko_security".id_seq;
CREATE SEQUENCE IF NOT EXISTS "joko_security".audit_session_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "joko_security".principal_session_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "joko_security".security_profile_id_seq START WITH 4 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "joko_security".consumer_api_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS "joko_security".seed_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE "joko_security".consumer_api (
    id BIGINT PRIMARY KEY,
    access_level VARCHAR(255),
    consumer_id VARCHAR(255),
    contact_name VARCHAR(255),
    document_number VARCHAR(255),
    name VARCHAR(255),
    secret VARCHAR(255)
);

CREATE TABLE "joko_security".keychain (
    id INT PRIMARY KEY,
    "value" VARCHAR(500)
);

CREATE TABLE "joko_security".principal_session (
    id BIGINT PRIMARY KEY,
    app_description VARCHAR(255),
    app_id VARCHAR(255),
    user_description VARCHAR(255),
    user_id VARCHAR(255),
    CONSTRAINT uk_muajvqvs1jntexdohty6hexrv UNIQUE (app_id, user_id)
);

CREATE TABLE "joko_security".audit_session (
    id BIGINT PRIMARY KEY,
    creation_date TIMESTAMP,
    remote_ip VARCHAR(255),
    user_agent VARCHAR(255),
    user_date TIMESTAMP,
    id_principal BIGINT,
    FOREIGN KEY (id_principal) REFERENCES "joko_security".principal_session(id)
);

CREATE TABLE "joko_security".security_profile (
    id BIGINT PRIMARY KEY,
    access_token_timeout_seconds INT,
    "key" VARCHAR(255),
    max_access_token_requests INT,
    max_number_of_connections INT,
    max_number_devices_user INT,
    name VARCHAR(255),
    refresh_token_timeout_seconds INT,
    revocable BOOLEAN
);

CREATE TABLE "joko_security".seed (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255),
    seed_secret VARCHAR(255)
);

CREATE TABLE "joko_security".tokens (
    id VARCHAR(255) PRIMARY KEY,
    expiration TIMESTAMP,
    issued_at TIMESTAMP,
    remote_ip VARCHAR(255),
    token_type VARCHAR(255),
    user_agent VARCHAR(255),
    user_id VARCHAR(255),
    security_profile_id BIGINT,
    FOREIGN KEY (security_profile_id) REFERENCES "joko_security".security_profile(id)
);
