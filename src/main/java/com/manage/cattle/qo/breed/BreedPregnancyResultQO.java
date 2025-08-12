package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedPregnancyResultQO extends PageQO {
    private String farmCode;
    private String farmZoneCode;
    private String cattleCode;
    private String resultDayStart;
    private String resultDayEnd;
    private String operaUser;
    private String result;
    private String remark;
}
