package com.manage.cattle.dto.healthDisease;

import com.manage.cattle.dto.CattleBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuarantineRegisterDTO extends CattleBaseDTO {
    private String registerDay;
    private String quarantineType;
    private String quarantineTypeValue;
    private String quarantineMethod;
    private String quarantineMethodValue;
    private String quarantineResult;
    private String veterinarian;
    private String remark;
}
