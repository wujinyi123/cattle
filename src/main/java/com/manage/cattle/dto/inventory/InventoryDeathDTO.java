package com.manage.cattle.dto.inventory;

import com.manage.cattle.dto.BaseDTO;
import com.manage.cattle.dto.base.CattleDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryDeathDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String cattleCode;
    private String cattleInfo;
    private String reason;
    private String handleMethod;
    private String deathDay;
}
