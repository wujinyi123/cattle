package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

@Data
public class BreedPregnancyResultQO extends PageQO {
    private String registerId;
    private String farmName;
    private String farmZoneCode;
    private String cattleCode;
    private String dayStart;
    private String dayEnd;
    private String operaUser;
    private String result;
    private String remark;
}
