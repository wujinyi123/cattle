package com.manage.cattle.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
    private String username;
    private String password;
    private String token;
    private String name;
    private Long roleId;
}
