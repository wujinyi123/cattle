package com.manage.cattle.service;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.UserDTO;
import com.manage.cattle.qo.LoginQO;

public interface UserService {
    UserDTO login(LoginQO loginQO);

    UserDTO getCurrentUser();

    PageInfo<UserDTO> pageUser(String username, String name);
}
