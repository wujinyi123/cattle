package com.manage.cattle.dao.base;

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

    int setUserStatus(@Param("updateUser") String updateUser, @Param("status") String status, @Param("usernameList") List<String> usernameList);

    int resetPassword(@Param("updateUser") String updateUser, @Param("password") String password, @Param("usernameList") List<String> usernameList);

    int delUser(@Param("usernameList") List<String> usernameList);
}
