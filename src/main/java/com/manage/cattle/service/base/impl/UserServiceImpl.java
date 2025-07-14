package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.SysDao;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dao.common.CommonDao;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.SysJobDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.exception.LoginException;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.UserService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.UserUtil;
import com.manage.cattle.util.PermissionUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Resource
    private SysDao sysDao;

    @Resource
    private FarmDao farmDao;

    @Resource
    private CommonDao commonDao;

    @Override
    public UserDTO login(LoginQO qo) {
        if (StrUtil.isBlank(qo.getUsername()) || StrUtil.isBlank(qo.getPassword())) {
            throw new LoginException("账号密码不能为空");
        }
        UserDTO dto = userDao.login(qo);
        if (Objects.isNull(dto)) {
            throw new LoginException("密码错误");
        }
        dto.setJobObj(sysDao.getSysJob(dto.getJobCode()));
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", dto.getUsername());
        payload.put("name", dto.getName());
        payload.put("isSysAdmin", dto.getIsSysAdmin());
        payload.put("jobDTO", dto.getJobObj());
        String token = UserUtil.createToken(payload);
        dto.setToken(token);
        return dto;
    }

    @Override
    public UserDTO getCurrentUser() {
        String username = UserUtil.getUsername();
        UserDTO dto = userDao.getUser(username);
        dto.setTokenCreateTime(UserUtil.getTokenCreateTime());
        dto.setTokenExpireTime(UserUtil.getTokenExpireTime());
        dto.setJobObj(sysDao.getSysJob(dto.getJobCode()));
        List<FarmDTO> farmList = farmDao.listFarm(new FarmQO());
        setFarmInfo(dto, farmList);
        return dto;
    }

    @Override
    public PageInfo<UserDTO> pageUser(UserQO qo) {
        PageHelper.startPage(qo);
        PageInfo<UserDTO> pageInfo = new PageInfo<>(userDao.listUser(qo));
        List<FarmDTO> farmList = farmDao.listFarm(new FarmQO());
        for (UserDTO dto : pageInfo.getList()) {
            setFarmInfo(dto, farmList);
        }
        return pageInfo;
    }

    @Override
    public List<UserDTO> listUser(UserQO qo) {
        List<UserDTO> list = userDao.listUser(qo);
        List<FarmDTO> farmList = farmDao.listFarm(new FarmQO());
        for (UserDTO dto : list) {
            setFarmInfo(dto, farmList);
        }
        return list;
    }

    private void setFarmInfo(UserDTO dto, List<FarmDTO> farmList) {
        dto.setFarmObj(farmList.stream()
                .filter(item -> item.getFarmCode().equals(dto.getFarmCode()))
                .findFirst()
                .orElse(null));
        if ("Y".equals(dto.getIsSysAdmin())) {
            dto.setFarmPowerList(farmList);
        } else {
            Set<String> farmCodeTemp = new HashSet<>();
            farmCodeTemp.addAll(CommonUtil.stringToList(dto.getFarmCode()));
            farmCodeTemp.addAll(CommonUtil.stringToList(dto.getFarmPower()));
            dto.setFarmPowerList(farmList.stream()
                    .filter(item -> farmCodeTemp.contains(item.getFarmCode()))
                    .toList());
        }
        dto.setFarmPowerName(dto.getFarmPowerList().stream()
                .map(FarmDTO::getFarmName)
                .collect(Collectors.joining(",")));
    }

    @Override
    public UserDTO getUser(String username) {
        return userDao.getUser(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveUser(String type, UserDTO dto) {
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if ("add".equals(type)) {
            if (Objects.nonNull(userDao.getUser(dto.getUsername()))) {
                throw new BusinessException("账号已存在");
            }
            return userDao.addUser(dto);
        } else {
            return userDao.updateUser(dto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePassword(JSONObject jsonObject) {
        String username = UserUtil.getUsername();
        String password = jsonObject.get("password", String.class);
        String newPassword = jsonObject.get("newPassword", String.class);
        String confirmPassword = jsonObject.get("confirmPassword", String.class);
        if (StrUtil.isBlank(password) || StrUtil.isBlank(newPassword) || StrUtil.isBlank(confirmPassword)) {
            throw new BusinessException("新旧密码均不能为空");
        }
        UserDTO user = userDao.login(new LoginQO(username, password));
        if (Objects.isNull(user)) {
            throw new BusinessException("密码不正确");
        }
        if (!StrUtil.equals(newPassword, confirmPassword)) {
            throw new BusinessException("两次新密码不一致");
        }
        return userDao.updatePassword(username, newPassword);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePhone(JSONObject jsonObject) {
        String username = UserUtil.getUsername();
        String phone = jsonObject.get("phone", String.class);
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException("联系方式不能为空");
        }
        return userDao.updatePhone(username, phone);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delUser(List<String> usernameList) {
        PermissionUtil.onlySysAdmin();
        return userDao.delUser(usernameList);
    }
}
