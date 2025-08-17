package com.manage.cattle.dto.healthDisease;

import com.manage.cattle.dto.CattleBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImmunityRegisterDTO extends CattleBaseDTO {
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
