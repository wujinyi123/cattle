package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String owner;
    private String admin;
    private String employee;
    private String address;
    private String area;
    private String scale;
}
