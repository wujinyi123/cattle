package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

@Data
public class BreedRegisterQO extends PageQO {
    private String farmName;
    private String farmZoneCode;
    private String cattleCode;
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String breedingDay;
    private String breedingMethod;
    private String operateUser;
}
