package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;

@Data
public class CattleDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmEmployee;
    private String farmZoneId;
    private String farmZoneCode;
    private String cattleId;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String breedValue;
    private String sex;
    private String sexValue;
    private String birthday;
    private String remark;
}
