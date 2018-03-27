package io.github.jokoframework.myproject.basic.repositories;

import io.github.jokoframework.myproject.basic.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Address Data Access
 *
 * @author bsandoval
 */
@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

}
