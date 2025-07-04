package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmQO extends PageQO {
    private String farmName;
}
