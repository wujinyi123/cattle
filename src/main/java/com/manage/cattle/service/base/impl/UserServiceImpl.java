package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.exception.LoginException;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.UserService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.JWTUtil;
import com.manage.cattle.util.PermissionUtil;
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

    @Resource
    private FarmDao farmDao;

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
        UserDTO userDTO = userDao.getUser(username);
        List<FarmDTO> farmList = farmDao.listFarm(new FarmQO());
        userDTO.setFarmList(farmList.stream().filter(dto -> username.equals(dto.getOwner())
                        || CommonUtil.stringToList(dto.getAdmin()).contains(username)
                        || CommonUtil.stringToList(dto.getEmployee()).contains(username))
                .toList());
        return userDTO;
    }

    @Override
    public PageInfo<UserDTO> pageUser(UserQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(userDao.listUser(qo));
    }

    @Override
    public List<UserDTO> listUser() {
        UserQO qo = new UserQO();
        qo.setStatus("incumbent");
        return userDao.listUser(qo);
    }

    @Override
    public UserDTO getUser(String username) {
        return userDao.getUser(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveUser(String type, UserDTO dto) {
        PermissionUtil.onlySysAdmin();
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
        PermissionUtil.onlySysAdmin();
        String username = JWTUtil.getUsername();
        return userDao.setUserStatus(username, status, usernameList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int resetPassword(List<String> usernameList) {
        PermissionUtil.onlySysAdmin();
        String username = JWTUtil.getUsername();
        return userDao.resetPassword(username, defaultPassword, usernameList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delUser(List<String> usernameList) {
        PermissionUtil.onlySysAdmin();
        return userDao.delUser(usernameList);
    }
}
