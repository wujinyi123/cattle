package com.manage.cattle.dto.breed;

import lombok.Data;

@Data
public class BreedRegisterDTO extends BreedBaseDTO {
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String frozenSemenBreedValue;
    private String breedingDay;
    private String breedingMethod;
    private String breedingMethodValue;
    private String operateUser;
}
