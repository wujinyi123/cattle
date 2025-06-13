package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.RoleDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;

import java.util.List;

public interface UserService {
    List<RoleDTO> listRole();

    UserDTO login(LoginQO loginQO);

    UserDTO getCurrentUser();

    PageInfo<UserDTO> pageUser(UserQO userQO);
}
