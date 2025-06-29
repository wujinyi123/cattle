package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

@Data
public class BreedPregnancyCheckQO extends PageQO {
    private String registerId;
    private String farmName;
    private String farmZoneCode;
    private String cattleCode;
    private String checkDayStart;
    private String checkDayEnd;
    private String checkUser;
    private String result;
}
