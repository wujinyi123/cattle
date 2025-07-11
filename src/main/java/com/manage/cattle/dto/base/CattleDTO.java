package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmZoneCode;
    private String farmZoneName;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String breedValue;
    private String sex;
    private String color;
    private String birthday;
    private String age;
    private String remark;
}
