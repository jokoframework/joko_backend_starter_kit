INSERT INTO basic.country (id, description) VALUES ('PY',
'Paraguay');
INSERT INTO basic.country (id, description) VALUES ('AR', 'Argentina');
INSERT INTO basic.country (id, description) VALUES ('BR', 'Brasil');
INSERT INTO basic.country (id, description) VALUES ('UY', 'Uruguay');

INSERT INTO profile.user (username, password, created, profile)
VALUES ( 'admin', '$2a$06$MRQTEuDm5qsu4Rz952Ck5Oc4rsL9busImPxAzql.QY43qnSp4bWgG', now(), 'ADMIN');