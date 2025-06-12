package com.manage.cattle.dao;

import com.manage.cattle.dto.UserDTO;
import com.manage.cattle.qo.LoginQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    UserDTO login(LoginQO loginQO);

    UserDTO getUser(@Param("username") String username);

    List<UserDTO> listUser(@Param("username") String username, @Param("name") String name);


}
