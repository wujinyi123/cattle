package com.manage.cattle.dto.breed;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedRegisterDTO extends BreedBaseDTO {
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String frozenSemenBreedValue;
    private String breedingDay;
    private String firstCheckDay;
    private String reCheckDay;
    private String expectedDay;
    private String breedingMethod;
    private String breedingMethodValue;
    private String operateUser;
}
