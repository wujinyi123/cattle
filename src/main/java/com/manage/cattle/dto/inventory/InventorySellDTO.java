package com.manage.cattle.dto.inventory;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventorySellDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String cattleCode;
    private String cattleInfo;
    private String buyerInfo;
    private Integer price;
    private String quarantineCertificate;
    private String sellDay;
}
