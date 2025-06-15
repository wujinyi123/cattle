package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;

@Data
public class UserQO extends PageQO {
    private String username;
    private String name;
    private String job;
    private String phone;
    private String isSysAdmin;
    private String status;
}
