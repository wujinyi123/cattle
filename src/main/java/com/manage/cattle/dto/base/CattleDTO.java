package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmEmployee;
    private String farmZoneCode;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String breedValue;
    private String sex;
    private String sexValue;
    private String birthday;
    private String remark;
}
