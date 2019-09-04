package io.github.jokoframework.myproject.web.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.github.jokoframework.myproject.basic.dto.CountryDTO;
import io.github.jokoframework.myproject.basic.service.CountryService;

import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
@RestController
public class CountryController {

    private final CountryService basicService;

    @Autowired
    public CountryController(CountryService basicService){
        this.basicService=basicService;
    }
    @ApiOperation(value = "Lista paises",
            notes = "Lista de paises registrados o vacio si no hay nada",
            position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Paises disponibles"),
    })
    @RequestMapping(value = ApiPaths.COUNTRIES, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
            paramType = "header", required = false, value = "Access Token")

    public List<CountryDTO> listAll(){
        return basicService.listCountries();
    }
}
