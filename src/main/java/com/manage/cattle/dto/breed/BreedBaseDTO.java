package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedBaseDTO extends BaseDTO {
    private String registerId;
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmZoneCode;
    private String cattleCode;
}
