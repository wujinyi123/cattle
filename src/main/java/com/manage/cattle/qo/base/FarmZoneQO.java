package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

@Data
public class FarmZoneQO extends PageQO {
    private String farmId;
    private String farmZoneCode;
    private String farmZoneRemark;
}
