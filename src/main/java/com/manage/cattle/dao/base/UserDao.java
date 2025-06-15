package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.UpdateUserDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    UserDTO login(LoginQO loginQO);

    UserDTO getUser(@Param("username") String username);

    List<UserDTO> listUser(UserQO userQO);

    int addUser(UserDTO userDTO);

    int updateUser(UserDTO userDTO);

    int setUserStatus(UpdateUserDTO dto);

    int resetPassword(UpdateUserDTO dto);

    int delUser(List<String> list);
}
