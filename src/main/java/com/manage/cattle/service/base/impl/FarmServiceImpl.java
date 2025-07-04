package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.FarmZoneQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.FarmService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.JWTUtil;
import com.manage.cattle.util.PermissionUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FarmServiceImpl implements FarmService {
    @Resource
    private FarmDao farmDao;

    @Resource
    private UserDao userDao;

    @Resource
    private CattleDao cattleDao;

    @Override
    public PageInfo<FarmDTO> pageFarm(FarmQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(farmDao.listFarm(qo));
    }

    @Override
    public List<FarmDTO> listFarm() {
        return farmDao.listFarm(new FarmQO());
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
        checkAdminEmployee(userList, dto);
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
    public int saveAdminEmployee(FarmDTO dto) {
        FarmDTO farmDTO = farmDao.getFarmById(dto.getFarmId());
        if (Objects.isNull(farmDTO)) {
            throw new BusinessException("牧场不存在");
        }
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        String username = JWTUtil.getUsername();
        if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner())) {
            throw new BusinessException("权限不足");
        }
        UserQO qo = new UserQO();
        qo.setStatus("incumbent");
        List<String> userList = userDao.listUser(qo).stream().map(UserDTO::getUsername).toList();
        checkAdminEmployee(userList, dto);
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return farmDao.saveAdminEmployee(dto);
    }

    private void checkAdminEmployee(List<String> userList, FarmDTO dto) {
        if (StringUtils.isNotBlank(dto.getAdmin())) {
            List<String> adminList = CommonUtil.stringToList(dto.getAdmin());
            adminList.sort(String::compareTo);
            List<String> errUser = adminList.stream().filter(item -> !userList.contains(item)).toList();
            if (errUser.size() > 0) {
                throw new BusinessException("管理员(" + String.join(",", errUser) + ")账号不正确");
            }
            dto.setAdmin(String.join(",", adminList));
        }
        if (StringUtils.isNotBlank(dto.getEmployee())) {
            List<String> employeeList = CommonUtil.stringToList(dto.getEmployee());
            employeeList.sort(String::compareTo);
            List<String> errUser = employeeList.stream().filter(item -> !userList.contains(item)).toList();
            if (errUser.size() > 0) {
                throw new BusinessException("员工(" + String.join(",", errUser) + ")账号不正确");
            }
            dto.setAdmin(String.join(",", employeeList));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delFarm(List<String> farmIds) {
        PermissionUtil.onlySysAdmin();
        return farmDao.delFarm(farmIds);
    }

    @Override
    public PageInfo<FarmZoneDTO> pageFarmZone(FarmZoneQO qo) {
        PageHelper.startPage(qo);
        PageInfo<FarmZoneDTO> pageInfo = new PageInfo<>(farmDao.listFarmZone(qo));
        if (pageInfo.getList().size() > 0) {
            List<String> farmZoneCodeList = pageInfo.getList().stream().map(FarmZoneDTO::getFarmZoneCode).toList();
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCodeList(farmZoneCodeList);
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            for (FarmZoneDTO dto : pageInfo.getList()) {
                dto.setCurrentSize((int) cattleList.stream().filter(item -> dto.getFarmZoneCode().equals(item.getFarmZoneCode())).count());
            }
        }
        return pageInfo;
    }

    @Override
    public List<FarmZoneDTO> listFarmZone(String farmId) {
        FarmZoneQO qo = new FarmZoneQO();
        qo.setFarmId(farmId);
        return farmDao.listFarmZone(qo);
    }

    @Override
    public FarmZoneDTO getFarmZone(String farmZoneCode) {
        return farmDao.getFarmZone(farmZoneCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveFarmZone(String type, FarmZoneDTO dto) {
        FarmDTO farmDTO = farmDao.getFarmById(dto.getFarmId());
        if (Objects.isNull(farmDTO)) {
            throw new BusinessException("牧场不存在");
        }
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        String username = JWTUtil.getUsername();
        List<String> adminList = CommonUtil.stringToList(farmDTO.getAdmin());
        if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner()) && !adminList.contains(username)) {
            throw new BusinessException("权限不足");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if ("add".equals(type)) {
            if (Objects.nonNull(farmDao.getFarmZone(dto.getFarmZoneCode()))) {
                throw new BusinessException("圈舍编号已存在");
            }
            return farmDao.addFarmZone(dto);
        } else {
            return farmDao.updateFarmZone(dto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delFarmZone(List<String> farmZoneCodeList) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        String username = JWTUtil.getUsername();
        FarmZoneQO qo = new FarmZoneQO();
        qo.setFarmZoneCodeList(farmZoneCodeList);
        List<FarmZoneDTO> farmZoneList = farmDao.listFarmZone(qo);
        for (FarmZoneDTO dto : farmZoneList) {
            List<String> adminList = CommonUtil.stringToList(dto.getFarmAdmin());
            if (!"Y".equals(isSysAdmin) && !username.equals(dto.getFarmOwner()) && !adminList.contains(username)) {
                throw new BusinessException("权限不足");
            }
        }
        return farmDao.delFarmZone(farmZoneCodeList);
    }
}
