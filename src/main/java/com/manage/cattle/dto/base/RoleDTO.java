package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;

@Data
public class RoleDTO extends BaseDTO {
    private String roleCode;
    private String roleName;
    private String roleDesc;
}
