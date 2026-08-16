package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.basic.dto.NotificationDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import io.github.jokoframework.myproject.basic.mapper.NotificationMapper;
import io.github.jokoframework.myproject.basic.service.NotificationService;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.exceptions.NotificationException;
import io.github.jokoframework.myproject.web.request.NotificationCreateDTO;
import io.github.jokoframework.myproject.web.response.CreateNotificationResponseDTO;
import io.github.jokoframework.myproject.web.response.NotificationTypesResponseDTO;
import io.github.jokoframework.myproject.web.response.ServiceResponseDTO;
import io.github.jokoframework.security.constantes.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    public static final String JOKO_STARTER_KIT_VERSION_HEADER = "X-JOKO-STARTER-KIT-VERSION";
    public static final String JOKO_STARTER_KIT_VERSION = "2.0";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Operation(summary = "Obtener tipos de notificaciones",
            description = "Obtiene todos los tipos de notificaciones disponibles. Ej: SYSTEM, EMAIL, PUSH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de notificaciones recuperados exitosamente")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_TYPE, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false, description = "Versión")
    public ResponseEntity<?> getNotificationTypes() {
        NotificationTypesResponseDTO response = new NotificationTypesResponseDTO();
        response.setSuccess(true);
        response.setMessage("Tipos de notificaciones recuperados exitosamente");
        response.setTypes(notificationService.getNotificationTypes());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener notificaciones del usuario",
            description = "Obtiene todas las notificaciones para el usuario especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones recuperadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_BY_USER, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "Token de acceso del usuario")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false, description = "Versión")
    public ResponseEntity<?> getUserNotifications(
            @Parameter(description = "ID del usuario") @PathVariable("userId") Long userId) {
        return new ResponseEntity<>(notificationService.getUserNotifications(userId), HttpStatus.OK);
    }

    @Operation(summary = "Crear notificación", description = "Crea una nueva notificación para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la notificación inválidos")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_USER, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "Token de acceso del usuario")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false, description = "Versión")
    public ResponseEntity<?> createNotification(
            @Parameter(description = "ID del usuario") @PathVariable("userId") Long userId,
            @RequestBody @Valid NotificationCreateDTO request) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(userId);
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setCategory(request.getCategory());
        notification.setChannel(request.getChannel());

        NotificationEntity created = notificationService.create(notification);
        NotificationDTO dto = notificationMapper.toDTO(created);

        CreateNotificationResponseDTO response = new CreateNotificationResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificación creada exitosamente");
        response.setData(dto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar notificación",
            description = "Elimina una notificación específica para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada o no pertenece al usuario")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_USER_BY_ID, method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "Token de acceso del usuario")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false, description = "Versión")
    public ResponseEntity<?> deleteNotification(
            @Parameter(description = "ID del usuario") @PathVariable("userId") Long userId,
            @Parameter(description = "ID de la notificación") @PathVariable("notificationId") Long notificationId)
            throws NotificationException {
        notificationService.deleteNotification(notificationId, userId);
        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificación eliminada exitosamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Marcar notificación como leída",
            description = "Marca una notificación específica como leída para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada o no pertenece al usuario")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_USER_READ, method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "Token de acceso del usuario")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false, description = "Versión")
    public ResponseEntity<?> markAsRead(
            @Parameter(description = "ID del usuario") @PathVariable("userId") Long userId,
            @Parameter(description = "ID de la notificación") @PathVariable("notificationId") Long notificationId)
            throws NotificationException {
        notificationService.markAsRead(notificationId, userId);
        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificación marcada como leída exitosamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
