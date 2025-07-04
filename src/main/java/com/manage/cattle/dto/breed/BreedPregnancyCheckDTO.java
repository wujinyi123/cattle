package com.manage.cattle.dto.breed;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedPregnancyCheckDTO extends BreedBaseDTO {
    private String checkDay;
    private String checkUser;
    private String result;
}
