package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;

@Data
public class BreedRegisterDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmEmployee;
    private String farmZoneId;
    private String farmZoneCode;
    private String cattleId;
    private String cattleCode;
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String breedingDay;
    private String breedingMethod;
    private String operateUser;
}
