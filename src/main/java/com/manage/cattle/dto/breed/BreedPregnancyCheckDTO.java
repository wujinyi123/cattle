package com.manage.cattle.dto.breed;

import lombok.Data;

@Data
public class BreedPregnancyCheckDTO extends BreedBaseDTO {
    private String checkDay;
    private String checkUser;
    private String result;
}
