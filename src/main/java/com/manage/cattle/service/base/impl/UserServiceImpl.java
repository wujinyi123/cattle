package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.UpdateUserDTO;
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
import org.springframework.transaction.annotation.Transactional;

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
    public UserDTO login(LoginQO qo) {
        if (StringUtils.isAnyBlank(qo.getUsername(), qo.getPassword())) {
            throw new LoginException("账号密码不能为空");
        }
        UserDTO dto = userDao.login(qo);
        if (Objects.isNull(dto)) {
            throw new LoginException("密码错误");
        }
        Map<String, String> payload = Map.of("username", dto.getUsername(), "name", dto.getName(), "isSysAdmin", dto.getIsSysAdmin());
        String token = JWTUtil.createToken(payload);
        dto.setToken(token);
        return dto;
    }

    @Override
    public UserDTO getCurrentUser() {
        String username = JWTUtil.getUsername();
        return userDao.getUser(username);
    }

    @Override
    public PageInfo<UserDTO> pageUser(UserQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(userDao.listUser(qo));
    }

    @Override
    public UserDTO getUser(String username) {
        return userDao.getUser(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveUser(String type, UserDTO dto) {
        onlySysAdminAction();
        String username = JWTUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if ("add".equals(type)) {
            if (Objects.nonNull(userDao.getUser(dto.getUsername()))) {
                throw new BusinessException("账号已存在");
            }
            dto.setPassword(defaultPassword);
            return userDao.addUser(dto);
        } else {
            return userDao.updateUser(dto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int setUserStatus(String status, List<String> usernameList) {
        onlySysAdminAction();
        String username = JWTUtil.getUsername();
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUpdateUser(username);
        dto.setStatus(status);
        dto.setUsernameList(usernameList);
        return userDao.setUserStatus(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int resetPassword(List<String> usernameList) {
        onlySysAdminAction();
        String username = JWTUtil.getUsername();
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUpdateUser(username);
        dto.setPassword(defaultPassword);
        dto.setUsernameList(usernameList);
        return userDao.resetPassword(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delUser(List<String> usernameList) {
        onlySysAdminAction();
        return userDao.delUser(usernameList);
    }

    private void onlySysAdminAction() {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin)) {
            throw new BusinessException("仅系统管理员操作");
        }
    }
}
