package com.manage.cattle.dto.breed;

import lombok.Data;

@Data
public class BreedPregnancyResultDTO extends BreedBaseDTO {
    private String day;
    private String operaUser;
    private String result;
    private String remark;
    private String children;
}
