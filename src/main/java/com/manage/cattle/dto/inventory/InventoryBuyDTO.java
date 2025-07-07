package com.manage.cattle.dto.inventory;

import com.manage.cattle.dto.BaseDTO;
import com.manage.cattle.dto.base.CattleDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryBuyDTO extends CattleDTO {
    private String source;
    private String quarantineCertificate;
    private String buyDay;
}
