package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dao.common.CommonDao;
import com.manage.cattle.dto.base.FarmDTO;
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
import com.manage.cattle.util.JWTUtil;
import com.manage.cattle.util.PermissionUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Value("${default.password}")
    private String defaultPassword;

    @Resource
    private UserDao userDao;

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
    public List<UserDTO> listUser(UserQO qo) {
        return userDao.listUser(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> importUser(String requireFields, List<UserDTO> list) {
        PermissionUtil.onlySysAdmin();
        String username = JWTUtil.getUsername();
        String[] requireFieldArr = requireFields.split(",");
        SysConfigQO qo = new SysConfigQO();
        qo.setCodeList(Arrays.asList("isSysAdmin", "userStatus"));
        Map<String, String> codeMap = commonDao.listSysConfig(qo).stream().collect(Collectors.toMap(dto -> dto.getCode() + "#" + dto.getValue(),
                SysConfigDTO::getKey, (key1, key2) -> key2));
        List<String> errList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            UserDTO dto = list.get(i);
            String address = "第" + (i + 2) + "行：";
            if (!CommonUtil.checkRequire(requireFieldArr, dto)) {
                errList.add(address + "必填项不能为空");
                continue;
            }
            dto.setIsSysAdmin(codeMap.get("isSysAdmin#" + dto.getIsSysAdminValue()));
            dto.setStatus(codeMap.get("userStatus#" + dto.getStatusValue()));
            if (StrUtil.isBlank(dto.getIsSysAdmin()) || StrUtil.isBlank(dto.getStatus())) {
                errList.add(address + "是否系统管理员或状态错误");
                continue;
            }
            if (userDao.getUser(dto.getUsername()) != null) {
                errList.add(address + "账号已存在");
                continue;
            }
            dto.setCreateUser(username);
            dto.setUpdateUser(username);
            dto.setPassword(defaultPassword);
            if (userDao.addUser(dto) == 0) {
                errList.add(address + "添加失败");
            }
        }
        return errList;
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
    public int updatePassword(JSONObject jsonObject) {
        String username = JWTUtil.getUsername();
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
        String username = JWTUtil.getUsername();
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
