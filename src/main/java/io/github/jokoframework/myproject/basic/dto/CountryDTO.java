package io.github.jokoframework.myproject.basic.dto;

import io.github.jokoframework.utils.dto_mapping.BaseDTO;

/**
 * Created by danicricco on 2/25/18.
 */
public class CountryDTO implements BaseDTO {

    private String id;
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
