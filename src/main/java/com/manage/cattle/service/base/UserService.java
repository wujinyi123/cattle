package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;

import java.util.List;

public interface UserService {
    UserDTO login(LoginQO loginQO);

    UserDTO getCurrentUser();

    PageInfo<UserDTO> pageUser(UserQO userQO);

    UserDTO getUser(String username);

    int saveUser(String type, UserDTO userDTO);

    int setUserStatus(String status, List<String> usernameList);

    int resetPassword(List<String> usernameList);

    int delUser(List<String> usernameList);
}
