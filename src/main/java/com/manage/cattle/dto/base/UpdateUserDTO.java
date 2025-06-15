package com.manage.cattle.dto.base;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserDTO {
    private String updateUser;
    private String password;
    private String status;
    private List<String> usernameList;
}
