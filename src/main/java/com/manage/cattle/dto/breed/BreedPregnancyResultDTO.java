package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.base.CattleDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedPregnancyResultDTO extends BreedBaseDTO {
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
