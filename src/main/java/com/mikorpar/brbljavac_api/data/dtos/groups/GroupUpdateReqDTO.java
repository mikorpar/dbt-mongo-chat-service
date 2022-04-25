package com.mikorpar.brbljavac_api.data.dtos.groups;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class GroupUpdateReqDTO {

    @NotBlank(message = "name property is mandatory")
    private String name;

    @UniqueElements
    @NotEmpty(message = "users array must not be empty")
    private List<@NotBlank String> users;
}
