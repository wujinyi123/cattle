package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

import java.util.List;

@Data
public class CattleQO extends PageQO {
    private String farmId;
    private String farmZoneId;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String sex;
    private String birthday;
    private String remark;
    private List<String> cattleIds;
    private List<String> cattleCodes;
}
