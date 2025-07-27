package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleChangeZoneQO extends PageQO {
    private String farmCode;
    private String oldFarmZoneCode;
    private String newFarmZoneCode;
    private String cattleCode;
    private String remark;
    private String operateUser;
    private String operateTimeStart;
    private String operateTimeEnd;
}
