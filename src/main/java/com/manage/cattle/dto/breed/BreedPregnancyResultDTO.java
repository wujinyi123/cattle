package com.manage.cattle.dto.breed;

import com.manage.cattle.dto.base.CattleDTO;
import lombok.Data;

import java.util.List;

@Data
public class BreedPregnancyResultDTO extends BreedBaseDTO {
    private String resultDay;
    private String operaUser;
    private String result;
    private String resultValue;
    private String remark;
    private String childrenIds;
    private List<CattleDTO> children;
}
