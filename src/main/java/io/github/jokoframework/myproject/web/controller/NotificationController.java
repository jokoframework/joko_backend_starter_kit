package io.github.jokoframework.myproject.web.controller;

import io.swagger.annotations.*;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.basic.dto.NotificationResponseDTO;
import io.github.jokoframework.myproject.basic.dto.NotificationTypeDTO;
import io.github.jokoframework.myproject.basic.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * Created by FedeTraversi on 4/16/25.
 */

@RestController
public class NotificationController {

    private final NotificationService notificationService;  //Logica de negocios

    public NotificationController(NotificationService notificationService) {    //Servicio
        this.notificationService = notificationService;
    }
    //Descripcion de la API
    @ApiOperation(value = "Obtener notificaciones",     
            notes = "Obtiene las notificaciones aleatorias para un usuario específico", 
            position = 1)
    //Respuestas de la API
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notificaciones recuperadas exitosamente"),
    })
    //Paths de la API
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_BY_USER,     //Path de la Notificacion por usuario
            method = RequestMethod.GET,                         //Api tipo GET
            produces = MediaType.APPLICATION_JSON_VALUE)        //Devuelve un JSON
    public NotificationResponseDTO getUserNotifications(        //Parametros de la API
            @ApiParam(value = "ID del usuario", required = true) 
            @PathVariable("userId") String userId) {
        return notificationService.getUserNotifications(userId);
    }

    @ApiOperation(value = "Obtener tipos de notificaciones",
            notes = "Obtiene todos los tipos de notificaciones disponibles en el sistema",
            position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tipos de notificaciones recuperados exitosamente"),
    })
    @RequestMapping(value = ApiPaths.NOTIFICATIONS_TYPE,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NotificationTypeDTO> getNotificationTypes() {
        return notificationService.getNotificationTypes();
    }
}