package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;

@Data
public class UserDTO extends BaseDTO {
    private String username;
    private String password;
    private String token;
    private String name;
    private String job;
    private String phone;
    private String isSysAdmin;
    private String status;
}
