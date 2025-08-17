package com.manage.cattle.qo.breed;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedFrozenSemenQO extends PageQO {
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String sexControl;
    private String color;
    private String fatherCode;
    private String motherCode;
}
