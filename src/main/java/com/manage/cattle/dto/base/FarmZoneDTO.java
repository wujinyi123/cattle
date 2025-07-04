package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;

@Data
public class FarmZoneDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmZoneId;
    private String farmZoneCode;
    private String farmZoneRemark;
    private Integer size;
    private Integer currentSize;
}
