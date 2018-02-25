package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.basic.dtos.CountryDTO;
import io.github.jokoframework.myproject.sys.utils.DTOUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by danicricco on 2/25/18.
 */
@Entity
@Table(name = "countries", schema = "basic")
public class CountryEntity implements DTOUtils.DTOConvertable {

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

    @Override
    public DTOUtils.BaseDTO toDTO() {
        return DTOUtils.fromEntityToDTO(this, new CountryDTO());
    }
}
