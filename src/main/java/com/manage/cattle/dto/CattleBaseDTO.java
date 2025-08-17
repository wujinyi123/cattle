package com.manage.cattle.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleBaseDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String farmZoneCode;
    private String cattleCode;
    private List<String> cattleCodeList;
}
