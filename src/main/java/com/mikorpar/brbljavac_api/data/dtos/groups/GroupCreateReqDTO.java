package com.mikorpar.brbljavac_api.data.dtos.groups;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class GroupCreateReqDTO {

    @NotBlank(message = "name property is mandatory")
    private String name;

    @Size(min = 2)
    @UniqueElements
    @NotEmpty(message = "users array must not be empty")
    private List<@NotBlank String> users;
}
