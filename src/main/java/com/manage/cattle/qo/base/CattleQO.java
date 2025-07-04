package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleQO extends PageQO {
    private String farmName;
    private String farmZoneCode;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String sex;
    private String birthday;
    private String remark;
    private List<String> cattleCodeList;
    private List<String> farmZoneCodeList;
}
