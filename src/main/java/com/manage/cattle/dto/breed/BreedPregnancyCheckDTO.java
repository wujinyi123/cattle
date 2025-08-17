package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.CattleBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BreedPregnancyCheckDTO extends CattleBaseDTO {
    private String checkDay;
    private String checkUser;
    private String result;
}
