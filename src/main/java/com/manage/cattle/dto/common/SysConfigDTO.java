package com.manage.cattle.dto.common;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysConfigDTO extends BaseDTO {
    private String code;
    private String key;
    private String value;
    private Integer sort;
}
