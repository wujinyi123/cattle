package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysJobDTO extends BaseDTO {
    private String jobCode;
    private String jobName;
    private String jobDesc;
    private String jobPower;
}
