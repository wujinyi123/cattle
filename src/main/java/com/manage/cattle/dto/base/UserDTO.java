package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends BaseDTO {
    private String token;
    private String username;
    private String password;
    private String name;
    private String sex;
    private String isSysAdmin;
    private String jobCode;
    private String jobName;
    private SysJobDTO jobObj;
    private String farmCode;
    private String farmName;
    private FarmDTO farmObj;
    private String farmPower;
    private List<FarmDTO> farmPowerList;
    private String farmPowerName;
    private String phone;
    private String expireDate;
    private String title;
    private String tokenCreateTime;
    private String tokenExpireTime;
}
