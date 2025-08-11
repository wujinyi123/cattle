package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedBaseDTO extends BaseDTO {
    private String registerId;
    private List<String> registerIds;
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String farmZoneCode;
    private String cattleCode;
    private List<String> cattleCodeList;
}
