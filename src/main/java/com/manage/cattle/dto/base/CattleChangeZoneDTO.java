package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleChangeZoneDTO extends BaseDTO {
    private String farmCode;
    private String oldFarmZoneCode;
    private String newFarmZoneCode;
    private String cattleCodes;
    private String remark;
    private String operateUser;
    private String operateTime;
}
