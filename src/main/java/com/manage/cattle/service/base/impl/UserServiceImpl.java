package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.exception.LoginException;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.UserService;
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {
    @Value("${default.password}")
    private String defaultPassword;

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
    public PageInfo<UserDTO> pageUser(UserQO userQO) {
        PageHelper.startPage(userQO);
        return new PageInfo<>(userDao.listUser(userQO));
    }

    @Override
    public UserDTO getUser(String username) {
        return userDao.getUser(username);
    }

    @Override
    public int saveUser(String type, UserDTO userDTO) {
        UserDTO currentUser = getCurrentUser();
        if ("N".equals(currentUser.getIsSysAdmin())
                && !StringUtils.equals(currentUser.getUsername(), userDTO.getUsername())) {
            throw new BusinessException("非系统管理员仅能修改个人信息");
        }
        userDTO.setCreateUser(currentUser.getUsername());
        userDTO.setUpdateUser(currentUser.getUsername());
        if ("add".equals(type)) {
            if (Objects.nonNull(userDao.getUser(userDTO.getUsername()))) {
                throw new BusinessException("账号已存在");
            }
            userDTO.setPassword(defaultPassword);
            return userDao.addUser(userDTO);
        } else {
            return userDao.updateUser(userDTO);
        }
    }

    @Override
    public int setUserStatus(String status, List<String> usernameList) {
        String username = JWTUtil.getUsername();
        sysAdminAction(username);
        return userDao.setUserStatus(username, status, usernameList);
    }

    @Override
    public int resetPassword(List<String> usernameList) {
        String username = JWTUtil.getUsername();
        sysAdminAction(username);
        return userDao.resetPassword(username, defaultPassword, usernameList);
    }

    @Override
    public int delUser(List<String> usernameList) {
        sysAdminAction();
        return userDao.delUser(usernameList);
    }

    private void sysAdminAction() {
        sysAdminAction(JWTUtil.getUsername());
    }

    private void sysAdminAction(String username) {
        sysAdminAction(userDao.getUser(username));
    }

    private void sysAdminAction(UserDTO currentUser) {
        if ("N".equals(currentUser.getIsSysAdmin())) {
            throw new BusinessException("仅管理员可操作");
        }
    }
}
