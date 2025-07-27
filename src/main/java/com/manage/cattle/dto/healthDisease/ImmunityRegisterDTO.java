package com.manage.cattle.dto.healthDisease;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImmunityRegisterDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String farmZoneCode;
    private String cattleCode;
    private List<String> cattleCodeList;
    private String registerDay;
    private String immunityName;
    private String immunityNameValue;
    private String immunityMethod;
    private String immunityMethodValue;
    private String drugName;
    private String dose;
    private String veterinarian;
    private String remark;
}
