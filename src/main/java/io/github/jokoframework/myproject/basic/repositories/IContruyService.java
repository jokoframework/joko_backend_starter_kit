package io.github.jokoframework.myproject.basic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.jokoframework.myproject.basic.entities.CountryEntity;

/**
 * Created by danicricco on 2/25/18.
 */
public interface IContruyService extends
        JpaRepository<CountryEntity, String> {
}


