INSERT INTO "joko_security".security_profile
    (id, name, "key", access_token_timeout_seconds, refresh_token_timeout_seconds,
     max_access_token_requests, max_number_of_connections, max_number_devices_user, revocable)
VALUES
    (1, 'Default Profile', 'DEFAULT', 1800, 86400, 50, 5, 3, true),
    (2, 'Admin Profile', 'ADMIN', 3600, 172800, 100, 10, 5, true),
    (3, 'Mobile Profile', 'MOBILE', 900, 604800, 30, 3, 2, true);

INSERT INTO "joko_security".keychain (id, "value")
VALUES (1, 'ZGV2ZWxvcG1lbnQta2V5LWZvci1qb2tvLXNlY3VyaXR5LXN0YXJ0ZXIta2l0');

INSERT INTO "basic".country (id, description)
VALUES ('PY', 'Paraguay'),
       ('AR', 'Argentina'),
       ('BR', 'Brasil'),
       ('UY', 'Uruguay');

INSERT INTO "profile"."USER" (id, username, password, created, profile)
VALUES (1, 'admin', '$2a$06$MRQTEuDm5qsu4Rz952Ck5Oc4rsL9busImPxAzql.QY43qnSp4bWgG', CURRENT_TIMESTAMP, 'ADMIN');

INSERT INTO "basic".notification
    (id, title, message, created_date, read_date, is_read, user_id, category, channel)
VALUES
    (1, 'Bienvenido al sistema',
        'Esta es una notificación de ejemplo para demostrar el sistema de notificaciones.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'system'),
    (2, 'Sistema actualizado',
        'Se ha completado una actualización importante del sistema.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'app_updates'),
    (3, 'Alerta de seguridad',
        'Se detectó un intento de acceso no autorizado.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'alert', 'security'),
    (4, 'Mantenimiento programado',
        'El sistema entrará en mantenimiento en 30 minutos.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 'warning', 'system'),
    (5, 'Nueva funcionalidad disponible',
        'Se han agregado nuevas características a la plataforma.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'app_updates'),
    (6, 'Backup completado',
        'Se ha completado el respaldo de datos programado.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'system');
