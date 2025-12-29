package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedPregnancyCheckQO extends PageQO {
    private String farmCode;
    private String farmZoneCode;
    private String cattleCode;
    private List<String> cattleCodeList;
    private String checkDayStart;
    private String checkDayEnd;
    private String checkUser;
    private String result;
}
