package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.myproject.web.request.CsvExportRequestDTO;
import io.github.jokoframework.myproject.web.response.HeartBeatResponseDTO;
import io.github.jokoframework.myproject.web.response.UserResponseDTO;
import io.github.jokoframework.myproject.web.response.UsersResponseDTO;
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

import java.util.List;

@RestController
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Override
    @RequestMapping(value = ApiPaths.USERS_HEARTBEAT, method = RequestMethod.GET)
    public ResponseEntity<HeartBeatResponseDTO> getHearbeat() {
        return new ResponseEntity<>(getHeartBeatStatus(), HttpStatus.OK);
    }

    @Operation(summary = "Get user's profile", description = "Get user's profile based on username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @RequestMapping(value = ApiPaths.USERS_BY_NAME, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "User Access Token")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false,
            description = "Version")
    public ResponseEntity<UserResponseDTO> getUser(
            @Parameter(description = "user name") @PathVariable("username") String username)
            throws UserException {
        UserDTO userDTO = userManager.getByUsername(username);
        UserResponseDTO response = new UserResponseDTO(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get users", description = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved"),
            @ApiResponse(responseCode = "404", description = "Users not found")
    })
    @RequestMapping(value = ApiPaths.ROOT_USERS, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "User Access Token")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false,
            description = "Version")
    public ResponseEntity<UsersResponseDTO> getUsers() throws UserException {
        List<UserDTO> list = userManager.findAll();
        UsersResponseDTO response = new UsersResponseDTO(list);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Export user's list to csv", description = "Get all users and creates a csv file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Csv successfully created"),
            @ApiResponse(responseCode = "404", description = "Users not found")
    })
    @RequestMapping(value = ApiPaths.USERS_CSV, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = true,
            description = "User Access Token")
    @Parameter(name = JOKO_STARTER_KIT_VERSION_HEADER, in = ParameterIn.HEADER, required = false,
            description = "Version")
    public ResponseEntity<UsersResponseDTO> getUsersCsvList(@RequestBody @Valid CsvExportRequestDTO request)
            throws UserException {
        List<UserDTO> list = userManager.findAll();
        byte[] csv = userManager.exportUsersListToCsv(list, request.getColumns());

        UsersResponseDTO response = new UsersResponseDTO(list);
        response.setCsv(csv);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
