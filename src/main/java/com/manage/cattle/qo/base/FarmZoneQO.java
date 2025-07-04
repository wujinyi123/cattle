package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmZoneQO extends PageQO {
    private String farmId;
    private String farmZoneCode;
    private String farmZoneRemark;
    private List<String> farmZoneCodeList;
}
