package io.github.jokoframework.myproject.basic.dtos;

import io.github.jokoframework.myproject.sys.utils.DTOUtils;

/**
 * Created by danicricco on 2/25/18.
 */
public class CountryDTO implements DTOUtils.BaseDTO {

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
