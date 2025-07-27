package com.manage.cattle.dto.healthDisease;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuarantineRegisterDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String farmZoneCode;
    private String cattleCode;
    private List<String> cattleCodeList;
    private String registerDay;
    private String quarantineType;
    private String quarantineTypeValue;
    private String quarantineMethod;
    private String quarantineMethodValue;
    private String quarantineResult;
    private String veterinarian;
    private String remark;
}
