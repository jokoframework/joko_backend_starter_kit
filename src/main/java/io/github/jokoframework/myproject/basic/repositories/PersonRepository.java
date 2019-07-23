package io.github.jokoframework.myproject.basic.repositories;

import io.github.jokoframework.myproject.basic.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Person Data Access
 *
 * @author bsandoval
 */
@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long>, JpaSpecificationExecutor<PersonEntity> {
    PersonEntity getOne(Long personId);
    
    PersonEntity findByIdentificationNumber(String identificationNumber);
}
