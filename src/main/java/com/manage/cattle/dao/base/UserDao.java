package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    UserDTO login(LoginQO qo);

    UserDTO getUser(@Param("username") String username);

    List<UserDTO> listUser(UserQO qo);

    int addUser(UserDTO dto);

    int updateUser(UserDTO dto);

    int updatePassword(@Param("username") String username, @Param("password") String password);

    int updatePhone(@Param("username") String username, @Param("phone") String phone);

    int delUser(List<String> list);
}
