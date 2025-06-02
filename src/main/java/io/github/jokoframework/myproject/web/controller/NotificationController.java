package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.basic.dto.NotificationDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import io.github.jokoframework.myproject.basic.service.NotificationService;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.exceptions.NotificationException;
import io.github.jokoframework.myproject.web.request.NotificationCreateDTO;
import io.github.jokoframework.myproject.web.response.ServiceResponseDTO;
import io.github.jokoframework.myproject.web.response.NotificationTypesResponseDTO;
import io.github.jokoframework.myproject.web.response.CreateNotificationResponseDTO;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.myproject.basic.mapper.NotificationMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * API de Notificaciones
 *
 * Created by FedeTraversi on 4/16/25.
 */
@RestController
public class NotificationController {

    public static final String JOKO_STARTER_KIT_VERSION_HEADER = "X-JOKO-STARTER-KIT-VERSION";
    public static final String JOKO_STARTER_KIT_VERSION = "1.0";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @ApiOperation(value = "Obtener tipos de notificaciones",
            notes = "Obtiene todos los tipos de notificaciones disponibles. Ej: SYSTEM, EMAIL, PUSH", position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tipos de notificaciones recuperados exitosamente", 
                        response = NotificationTypesResponseDTO.class,
                        examples = @Example(value = {
                            @ExampleProperty(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                value = "{\n\"success\": true,\n\"message\": \"Tipos de notificaciones recuperados exitosamente\",\n\"types\": [{\n    \"name\": \"SYSTEM\",\n    \"category\": \"GENERAL\",\n    \"channel\": \"APP\"\n  },\n  {\n    \"name\": \"EMAIL\",\n    \"category\": \"EXTERNAL\",\n    \"channel\": \"EMAIL\"\n  }]}"
                            )
                        }))
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_TYPE, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Versión", defaultValue = JOKO_STARTER_KIT_VERSION)
    public ResponseEntity<?> getNotificationTypes() {
        NotificationTypesResponseDTO response = new NotificationTypesResponseDTO();
        response.setSuccess(true);
        response.setMessage("Tipos de notificaciones recuperados exitosamente");
        response.setTypes(notificationService.getNotificationTypes());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Obtener notificaciones del usuario",
            notes = "Obtiene todas las notificaciones para el usuario especificado", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notificaciones recuperadas exitosamente",
                        examples = @Example(value = {
                            @ExampleProperty(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                value = "{\n\"success\": true,\n\"message\": \"Notificaciones recuperadas exitosamente\",\n\"data\": [{\n    \"id\": \"1\",\n    \"title\": \"Bienvenido\",\n    \"message\": \"Bienvenido al sistema\",\n    \"category\": \"GENERAL\",\n    \"timestamp\": \"2025-05-03T10:15:30Z\",\n    \"channel\": \"APP\",\n    \"read\": false\n  }],\n\"metadata\": {\n    \"total\": 1,\n    \"timestamp\": \"2025-05-03T10:15:31Z\"\n  }}"
                            )
                        })),
            @ApiResponse(code = 404, message = "Usuario no encontrado")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_BY_USER, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Token de acceso del usuario"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Versión", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<?> getUserNotifications(
            @ApiParam(value = "ID del usuario", example = "123") @PathVariable("userId") Long userId) {
        return new ResponseEntity<>(notificationService.getUserNotifications(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "Crear notificación",
            notes = "Crea una nueva notificación para un usuario", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Notificación creada exitosamente",
                        examples = @Example(value = {
                            @ExampleProperty(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                value = "{\n\"success\": true,\n\"message\": \"Notificación creada exitosamente\",\n\"data\": {\n\"id\": \"1\",\n\"title\": \"Nueva notificación\",\n\"message\": \"Contenido de la notificación\",\n\"category\": \"GENERAL\",\n\"timestamp\": \"2025-05-03T10:15:30Z\",\n\"channel\": \"APP\",\n\"read\": false\n}\n}"
                            )
                        })),
            @ApiResponse(code = 400, message = "Datos de la notificación inválidos")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_USER, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Token de acceso del usuario"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Versión", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<?> createNotification(
            @ApiParam(value = "ID del usuario", example = "123") @PathVariable("userId") Long userId,
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

    @ApiOperation(value = "Eliminar notificación",
            notes = "Elimina una notificación específica para un usuario", position = 3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notificación eliminada exitosamente",
                        examples = @Example(value = {
                            @ExampleProperty(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                value = "{\n\"success\": true,\n\"message\": \"Notificación eliminada exitosamente\"\n}"
                            )
                        })),
            @ApiResponse(code = 404, message = "Notificación no encontrada o no pertenece al usuario")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_USER_BY_ID, method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Token de acceso del usuario"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Versión", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<?> deleteNotification(
            @ApiParam(value = "ID del usuario", example = "123") @PathVariable("userId") Long userId,
            @ApiParam(value = "ID de la notificación", example = "456") @PathVariable("notificationId") Long notificationId) throws NotificationException {
        notificationService.deleteNotification(notificationId, userId);
        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificación eliminada exitosamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Marcar notificación como leída",
            notes = "Marca una notificación específica como leída para un usuario", position = 4)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notificación marcada como leída exitosamente",
                        examples = @Example(value = {
                            @ExampleProperty(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                value = "{\n\"success\": true,\n\"message\": \"Notificación marcada como leída exitosamente\"\n}"
                            )
                        })),
            @ApiResponse(code = 404, message = "Notificación no encontrada o no pertenece al usuario")
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_USER_READ, method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "Token de acceso del usuario"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Versión", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<?> markAsRead(
            @ApiParam(value = "ID del usuario", example = "123") @PathVariable("userId") Long userId,
            @ApiParam(value = "ID de la notificación", example = "456") @PathVariable("notificationId") Long notificationId) throws NotificationException {
        notificationService.markAsRead(notificationId, userId);
        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificación marcada como leída exitosamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}