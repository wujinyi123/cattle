package com.manage.cattle.qo.inventory;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryBuyQO extends PageQO {
    private String farmCode;
    private String cattleCode;
    private String source;
    private String buyDayStart;
    private String buyDayEnd;
    private List<Integer> ids;
}
