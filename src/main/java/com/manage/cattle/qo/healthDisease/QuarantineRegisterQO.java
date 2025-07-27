package com.manage.cattle.qo.healthDisease;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuarantineRegisterQO extends PageQO {
    private String farmCode;
    private String farmZoneCode;
    private String cattleCode;
    private String registerDayStart;
    private String registerDayEnd;
    private String quarantineType;
    private String quarantineMethod;
    private String quarantineResult;
    private String veterinarian;
    private String remark;
}
