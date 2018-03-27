package io.github.jokoframework.myproject.basic.service;

import io.github.jokoframework.myproject.basic.dto.CountryDTO;

import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
public interface CountryService {

    public List<CountryDTO> listCountries();
}
