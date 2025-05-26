CREATE TABLE IF NOT EXISTS  basic.notification
(
   id integer PRIMARY KEY NOT NULL,
   title varchar(100),
   message varchar(500),
   created_date timestamp,
   read_date timestamp,
   is_read boolean,
   user_id integer,
   category varchar(50),
   channel varchar(50)
);

MERGE INTO joko_security.security_profile(id,access_token_timeout_seconds,
    "KEY",max_access_token_requests, max_number_of_connections, max_number_devices_user, name,
            refresh_token_timeout_seconds, revocable)
    KEY(id)
    VALUES (1,14440 ,'DEFAULT', 2, null, 1, 'Default configuration(testing purposes)',300, false);

MERGE INTO joko_security.security_profile(id,access_token_timeout_seconds,
    "KEY",max_access_token_requests, max_number_of_connections, max_number_devices_user, name,
            refresh_token_timeout_seconds, revocable)
    VALUES (2,0 ,'EXPIRATION_SECURITY_PROFILE', 2, null, 1, 'Default ' ||
     'configuration(testing purposes)',0, false);


MERGE INTO basic.country (id, description)
KEY(ID)
VALUES ('PY','Paraguay'),
 ('AR', 'Argentina'),
 ('BR', 'Brasil'),
 ('UY', 'Uruguay');

MERGE INTO profile."USER" (id,username, password, created, profile) KEY (ID)
VALUES (1, 'admin', '$2a$06$MRQTEuDm5qsu4Rz952Ck5Oc4rsL9busImPxAzql.QY43qnSp4bWgG', now(), 'ADMIN') ;


--Inserta ejemplos de notificaciones
MERGE INTO basic.notification (id, title, message, created_date, read_date, is_read, user_id, category, channel) KEY (ID)
VALUES 
    (1, 
    'Bienvenido al sistema', 
    'Esta es una notificación de ejemplo para demostrar el sistema de notificaciones.', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'system'),
    (2, 
    'Sistema actualizado', 
    'Se ha completado una actualización importante del sistema.', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'app_updates'),
    (3, 
    'Alerta de seguridad', 
    'Se detectó un intento de acceso no autorizado.', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'alert', 'security'),
    (4, 
    'Mantenimiento programado', 
    'El sistema entrará en mantenimiento en 30 minutos.', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 'warning', 'system'),
    (5, 
    'Nueva funcionalidad disponible', 
    'Se han agregado nuevas características a la plataforma.', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'app_updates'),
    (6, 
    'Backup completado', 
    'Se ha completado el respaldo de datos programado.', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 1, 'info', 'system');


