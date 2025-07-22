package com.manage.cattle.dto;

import lombok.Data;

@Data
public class BaseDTO {
    private Integer id;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
    private String importError;
}
