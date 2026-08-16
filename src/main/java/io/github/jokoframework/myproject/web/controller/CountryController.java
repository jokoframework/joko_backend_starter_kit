package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.basic.dto.CountryDTO;
import io.github.jokoframework.myproject.basic.service.CountryService;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.security.constantes.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CountryController {

    private final CountryService basicService;

    @Autowired
    public CountryController(CountryService basicService) {
        this.basicService = basicService;
    }

    @Operation(summary = "Lista paises", description = "Lista de paises registrados o vacio si no hay nada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paises disponibles")
    })
    @RequestMapping(value = ApiPaths.COUNTRIES, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(name = SecurityConstants.AUTH_HEADER_NAME, in = ParameterIn.HEADER, required = false,
            description = "Access Token")
    public List<CountryDTO> listAll() {
        return basicService.listCountries();
    }
}
