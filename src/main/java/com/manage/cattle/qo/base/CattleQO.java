package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleQO extends PageQO {
    private String farmCode;
    private String farmZoneCode;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String sex;
    private String color;
    private String birthdayStart;
    private String birthdayEnd;
    private String remark;
    private String source;
    private String breedStatus;
    private List<String> cattleCodeList;
    private List<String> farmZoneCodeList;
}
