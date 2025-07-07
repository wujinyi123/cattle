package com.manage.cattle.qo.inventory;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventorySellQO extends PageQO {
    private String farmName;
    private String cattleCode;
    private String buyerInfo;
    private String quarantineCertificate;
    private String sellDayStart;
    private String sellDayEnd;
    private List<Integer> ids;
}
