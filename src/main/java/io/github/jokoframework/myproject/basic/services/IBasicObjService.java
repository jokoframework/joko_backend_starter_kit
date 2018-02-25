package io.github.jokoframework.myproject.basic.services;

import io.github.jokoframework.myproject.basic.dtos.CountryDTO;

import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
public interface IBasicObjService {

    public List<CountryDTO> listCountries();
}
