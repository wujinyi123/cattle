package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends BaseDTO {
    private String username;
    private String password;
    private String token;
    private String name;
    private String title;
    private String job;
    private String phone;
    private String isSysAdmin;
    private String isSysAdminValue;
    private String status;
    private String statusValue;
    private List<FarmDTO> farmList;
}
