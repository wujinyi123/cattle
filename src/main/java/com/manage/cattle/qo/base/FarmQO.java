package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmQO extends PageQO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String address;
    private String area;
    private String scale;
    private List<String> farmCodeList;
}
