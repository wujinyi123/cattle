package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.FarmService;
import com.manage.cattle.util.JWTUtil;
import com.manage.cattle.util.PermissionUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class FarmServiceImpl implements FarmService {
    @Resource
    private FarmDao farmDao;

    @Resource
    private UserDao userDao;

    @Override
    public PageInfo<FarmDTO> pageFarm(FarmQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(farmDao.listFarm(qo));
    }

    @Override
    public FarmDTO getFarm(String farmId) {
        return farmDao.getFarmById(farmId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveFarm(String type, FarmDTO dto) {
        PermissionUtil.onlySysAdmin();
        UserQO qo = new UserQO();
        qo.setStatus("incumbent");
        List<String> userList = userDao.listUser(qo).stream().map(UserDTO::getUsername).toList();
        if (!userList.contains(dto.getOwner())) {
            throw new BusinessException("负责人账号不正确");
        }
        if (StringUtils.isNotBlank(dto.getAdmin())) {
            List<String> adminList = Arrays.stream(dto.getAdmin().split(",")).toList();
            List<String> errUser = adminList.stream().filter(item -> !userList.contains(item)).toList();
            throw new BusinessException("管理员(" + String.join(",", errUser) + ")账号不正确");
        }
        if (StringUtils.isNotBlank(dto.getEmployee())) {
            List<String> employeeList = Arrays.stream(dto.getEmployee().split(",")).toList();
            List<String> errUser = employeeList.stream().filter(item -> !userList.contains(item)).toList();
            throw new BusinessException("员工(" + String.join(",", errUser) + ")账号不正确");
        }
        String username = JWTUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if ("add".equals(type)) {
            if (Objects.nonNull(farmDao.getFarm(dto.getFarmName()))) {
                throw new BusinessException("牧场已存在");
            }
            dto.setFarmId(IdUtil.getSnowflakeNextIdStr());
            return farmDao.addFarm(dto);
        } else {
            return farmDao.updateFarm(dto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delFarm(List<String> farmIds) {
        PermissionUtil.onlySysAdmin();
        return farmDao.delFarm(farmIds);
    }
}
