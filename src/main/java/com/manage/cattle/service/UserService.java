package com.manage.cattle.service;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.UserDTO;
import com.manage.cattle.qo.LoginQO;

public interface UserService {
    UserDTO login(LoginQO loginQO);

    UserDTO getCurrentUser();

    PageInfo<UserDTO> pageUser(int pageNum, int pageSize, String username, String name);
}
