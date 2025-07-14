package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmOwner;
    private String address;
    private String area;
    private String scale;
}
