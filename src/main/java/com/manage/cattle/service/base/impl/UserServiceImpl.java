package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.RoleDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.exception.LoginException;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.UserService;
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public List<RoleDTO> listRole() {
        return userDao.listRole();
    }

    @Override
    public UserDTO login(LoginQO loginQO) {
        if (StringUtils.isAnyBlank(loginQO.getUsername(), loginQO.getPassword())) {
            throw new LoginException("账号密码不能为空");
        }
        UserDTO userDTO = userDao.login(loginQO);
        if (Objects.isNull(userDTO)) {
            throw new LoginException("密码错误");
        }
        String token = JWTUtil.createToken(Map.of("username", userDTO.getUsername(), "name", userDTO.getName()));
        userDTO.setToken(token);
        return userDTO;
    }

    @Override
    public UserDTO getCurrentUser() {
        String username = JWTUtil.getUsername();
        return userDao.getUser(username);
    }

    @Override
    public PageInfo<UserDTO> pageUser(UserQO userQO) {
        PageHelper.startPage(userQO);
        return new PageInfo<>(userDao.listUser(userQO));
    }
}
