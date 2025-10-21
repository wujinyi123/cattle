package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String farmZoneCode;
    private String cattleCode;
    private String cattleName;
    private String breed;
    private String breedValue;
    private String sex;
    private Integer year;
    private Integer month;
    private Integer day;
    private String color;
    private String birthday;
    private String age;
    private String remark;
    private String source;
    private String breedingDay;
    private String firstCheckDay;
    private String actualFirstCheckDay;
    private String firstCheckResult;
    private String reCheckDay;
    private String actualReCheckDay;
    private String reCheckResult;
    private Integer pregnancyDays;
    private String expectedDay;
    private String actualExpectedDay;
    private String expectedResult;
    private String breedStatus;
}
