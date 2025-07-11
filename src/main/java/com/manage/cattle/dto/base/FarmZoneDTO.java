package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FarmZoneDTO extends BaseDTO {
    private String farmCode;
    private String farmName;
    private String farmZoneCode;
    private String farmZoneName;
    private String farmZoneRemark;
    private Integer size;
    private Integer currentSize;
}
