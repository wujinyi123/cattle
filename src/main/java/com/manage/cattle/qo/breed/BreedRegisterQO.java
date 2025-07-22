package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedRegisterQO extends PageQO {
    private String farmCode;
    private String farmZoneCode;
    private String cattleCode;
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String breedingDayStart;
    private String breedingDayEnd;
    private String breedingMethod;
    private String operateUser;
    private String registerId;
}
