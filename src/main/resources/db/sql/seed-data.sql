INSERT INTO basic.country (id, description) VALUES ('PY', 'Paraguay');
INSERT INTO basic.country (id, description) VALUES ('AR', 'Argentina');
INSERT INTO basic.country (id, description) VALUES ('BR', 'Brasil');
INSERT INTO basic.country (id, description) VALUES ('UY', 'Uruguay');
INSERT INTO basic.country (id, description) VALUES ('CO', 'Colombia');

INSERT INTO profile.user (username, password, created, profile)
VALUES ('admin', '$2a$06$MRQTEuDm5qsu4Rz952Ck5Oc4rsL9busImPxAzql.QY43qnSp4bWgG', now(), 'ADMIN');

--Inserta ejemplos de notificaciones
INSERT INTO basic.notification (id, title, message, created_date, is_read, user_id, category, channel)
VALUES 
    (nextval('basic.notification_id_seq'), 'Bienvenido al sistema', 'Esta es una notificación de ejemplo para demostrar el sistema de notificaciones.', 
     CURRENT_TIMESTAMP, false, 1, 'info', 'system'),
    (nextval('basic.notification_id_seq'), 'Sistema actualizado', 'Se ha completado una actualización importante del sistema.', 
     CURRENT_TIMESTAMP, false, 1, 'info', 'app_updates'),
    (nextval('basic.notification_id_seq'), 'Alerta de seguridad', 'Se detectó un intento de acceso no autorizado.', 
     CURRENT_TIMESTAMP, false, 1, 'alert', 'security'),
    (nextval('basic.notification_id_seq'), 'Mantenimiento programado', 'El sistema entrará en mantenimiento en 30 minutos.', 
     CURRENT_TIMESTAMP, true, 1, 'warning', 'system'),
    (nextval('basic.notification_id_seq'), 'Nueva funcionalidad disponible', 'Se han agregado nuevas características a la plataforma.', 
     CURRENT_TIMESTAMP, false, 1, 'info', 'app_updates'),
    (nextval('basic.notification_id_seq'), 'Backup completado', 'Se ha completado el respaldo de datos programado.', 
     CURRENT_TIMESTAMP, false, 1, 'info', 'system');
