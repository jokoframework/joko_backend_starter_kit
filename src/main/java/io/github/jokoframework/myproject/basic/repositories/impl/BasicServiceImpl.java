package io.github.jokoframework.myproject.basic.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.jokoframework.myproject.basic.dtos.CountryDTO;
import io.github.jokoframework.myproject.basic.entities.CountryEntity;
import io.github.jokoframework.myproject.basic.repositories.IContruyService;
import io.github.jokoframework.myproject.basic.services.IBasicObjService;
import io.github.jokoframework.myproject.sys.utils.DTOUtils;

import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
@Service
public class BasicServiceImpl implements IBasicObjService{

    private IContruyService documentTypeRepository;

    @Autowired
    public BasicServiceImpl(IContruyService documentTypeRepository){
        this.documentTypeRepository=documentTypeRepository;
    }
    @Override
    public List<CountryDTO> listCountries() {

        List<CountryEntity> documentTypeEntityList = documentTypeRepository
                .findAll();
        return DTOUtils.fromEntityToDTO(documentTypeEntityList,CountryDTO
                .class);
    }
}
