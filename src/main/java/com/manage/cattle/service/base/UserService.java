package com.manage.cattle.service.base;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.dto.common.ImportInfo;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;

import java.util.List;

public interface UserService {
    UserDTO login(LoginQO qo);

    UserDTO getCurrentUser();

    PageInfo<UserDTO> pageUser(UserQO qo);

    List<UserDTO> listUser(UserQO qo);

    List<String> importUser(String requireFields, List<UserDTO> list);

    UserDTO getUser(String username);

    int saveUser(String type, UserDTO dto);

    int setUserStatus(String status, List<String> usernameList);

    int resetPassword(List<String> usernameList);

    int updatePassword(JSONObject jsonObject);

    int updatePhone(JSONObject jsonObject);

    int delUser(List<String> usernameList);
}
