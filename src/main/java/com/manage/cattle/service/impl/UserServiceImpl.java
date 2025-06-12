package com.manage.cattle.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.UserDao;
import com.manage.cattle.dto.UserDTO;
import com.manage.cattle.exception.LoginException;
import com.manage.cattle.qo.LoginQO;
import com.manage.cattle.service.UserService;
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

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
    public PageInfo<UserDTO> pageUser(int pageNum, int pageSize, String username, String name) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(userDao.listUser(username, name));
    }
}
