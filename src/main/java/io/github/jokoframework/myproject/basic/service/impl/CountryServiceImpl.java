package io.github.jokoframework.myproject.basic.service.impl;

import io.github.jokoframework.myproject.basic.service.CountryService;
import io.github.jokoframework.utils.dto_mapping.DTOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.jokoframework.myproject.basic.dto.CountryDTO;
import io.github.jokoframework.myproject.basic.entities.CountryEntity;
import io.github.jokoframework.myproject.basic.repositories.CountryRepository;

import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
@Service
public class CountryServiceImpl implements CountryService {

    private CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository){
        this.countryRepository = countryRepository;
    }

    @Override
    public List<CountryDTO> listCountries() {

        List<CountryEntity> entityList = countryRepository
                .findAll();
        return DTOUtils.fromEntityToDTO(entityList,CountryDTO
                .class);
    }
}
