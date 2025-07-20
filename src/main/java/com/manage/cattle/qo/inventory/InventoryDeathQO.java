package com.manage.cattle.qo.inventory;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryDeathQO extends PageQO {
    private String farmCode;
    private String cattleCode;
    private String reason;
    private String handleMethod;
    private String deathDayStart;
    private String deathDayEnd;
    private List<Integer> ids;
}
