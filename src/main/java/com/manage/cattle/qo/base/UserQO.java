package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQO extends PageQO {
    private String username;
    private String name;
    private String sex;
    private String isSysAdmin;
    private String jobCode;
    private String farmCode;
    private String phone;
    private String expireDateStart;
    private String expireDateEnd;
    private String noExpire;
    private List<String> usernameList;
}
