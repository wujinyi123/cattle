package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmZoneDTO extends BaseDTO {
    private String farmId;
    private String farmName;
    private String farmOwner;
    private String farmAdmin;
    private String farmZoneCode;
    private String farmZoneRemark;
    private Integer size;
    private Integer currentSize;
}
