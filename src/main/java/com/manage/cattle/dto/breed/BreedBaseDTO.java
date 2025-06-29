package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;

@Data
public class BreedBaseDTO extends BaseDTO {
    private String registerId;
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmEmployee;
    private String farmZoneId;
    private String farmZoneCode;
    private String cattleId;
    private String cattleCode;
}
