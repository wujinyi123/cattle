package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.CattleBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedPregnancyResultDTO extends CattleBaseDTO {
    private String resultDay;
    private String operaUser;
    private String result;
    private String remark;
    private String childFarmZoneCode;
    private String childCattleCode;
    private String breed;
    private String breedValue;
    private String sex;
    private String color;
    private String weight;
}
