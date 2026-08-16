package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.basic.dto.CountryDTO;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import io.github.jokoframework.utils.dto_mapping.*;

/**
 * Created by danicricco on 2/25/18.
 */
@Entity
@Table(name = "country", schema = "basic")
public class CountryEntity extends BaseEntity<CountryDTO>{

    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private String id;

    @Column(name = "description")
    @NotNull
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String pId) {
        id = pId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }


}
