package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedFrozenSemenDTO extends BaseDTO {
    private String frozenSemenCode;
    private String frozenSemenBreed;
    private String frozenSemenBreedValue;
    private String sexControl;
    private String color;
    private String fatherCode;
    private String motherCode;
}
