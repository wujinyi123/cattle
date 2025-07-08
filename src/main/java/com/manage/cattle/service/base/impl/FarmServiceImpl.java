package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
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
import com.manage.cattle.util.UserUtil;
import com.manage.cattle.util.PermissionUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public List<FarmDTO> listFarm(FarmQO qo) {
        return farmDao.listFarm(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> importFarm(String requireFields, List<FarmDTO> list) {
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getUsername();
        String[] requireFieldArr = requireFields.split(",");
        List<String> errList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FarmDTO dto = list.get(i);
            String address = "第" + (i + 2) + "行：";
            if (!CommonUtil.checkRequire(requireFieldArr, dto)) {
                errList.add(address + "必填项不能为空");
                continue;
            }
            Set<String> usernameSet = new HashSet<>();
            usernameSet.add(dto.getOwner());
            usernameSet.addAll(CommonUtil.stringToList(dto.getAdmin()));
            UserQO qo = new UserQO();
            qo.setUsernameList(new ArrayList<>(usernameSet));
            List<UserDTO> userList = userDao.listUser(qo);
            List<String> usernameList = userList.stream().map(UserDTO::getUsername).toList();
            List<String> errorUsername = usernameSet.stream().filter(item -> !usernameList.contains(item)).toList();
            if (errorUsername.size() > 0) {
                errList.add(address + "账号(" + String.join(",", errorUsername) + ")不正确");
                continue;
            }
            if (farmDao.getFarm(dto.getFarmName()) != null) {
                errList.add(address + "牧场已存在");
                continue;
            }
            dto.setCreateUser(username);
            dto.setUpdateUser(username);
            dto.setFarmId(IdUtil.getSnowflakeNextIdStr());
            if (farmDao.addFarm(dto) == 0) {
                errList.add(address + "添加失败");
            }
        }
        return errList;
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
        checkAdmin(userList, dto);
        String username = UserUtil.getUsername();
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
    public int saveAdmin(FarmDTO dto) {
        FarmDTO farmDTO = farmDao.getFarmById(dto.getFarmId());
        if (Objects.isNull(farmDTO)) {
            throw new BusinessException("牧场不存在");
        }
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
        if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner())) {
            throw new BusinessException("权限不足");
        }
        UserQO qo = new UserQO();
        qo.setStatus("incumbent");
        List<String> userList = userDao.listUser(qo).stream().map(UserDTO::getUsername).toList();
        checkAdmin(userList, dto);
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return farmDao.saveAdmin(dto);
    }

    private void checkAdmin(List<String> userList, FarmDTO dto) {
        if (StrUtil.isNotBlank(dto.getAdmin())) {
            List<String> adminList = CommonUtil.stringToList(dto.getAdmin());
            adminList.sort(String::compareTo);
            List<String> errUser = adminList.stream().filter(item -> !userList.contains(item)).toList();
            if (errUser.size() > 0) {
                throw new BusinessException("管理员(" + String.join(",", errUser) + ")账号不正确");
            }
            dto.setAdmin(String.join(",", adminList));
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
        setCurrentSize(pageInfo.getList());
        return pageInfo;
    }

    @Override
    public List<FarmZoneDTO> listFarmZone(FarmZoneQO qo) {
        List<FarmZoneDTO> list = farmDao.listFarmZone(qo);
        setCurrentSize(list);
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> importFarmZone(String requireFields, List<FarmZoneDTO> list) {
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
        String[] requireFieldArr = requireFields.split(",");
        List<String> errList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FarmZoneDTO dto = list.get(i);
            String address = "第" + (i + 2) + "行：";
            if (!CommonUtil.checkRequire(requireFieldArr, dto)) {
                errList.add(address + "必填项不能为空");
                continue;
            }
            FarmDTO farmDTO = farmDao.getFarm(dto.getFarmName());
            if (farmDTO == null) {
                errList.add(address + "牧场不存在");
                continue;
            }
            if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner()) && !CommonUtil.stringToList(farmDTO.getAdmin()).contains(username)) {
                errList.add(address + "权限不足");
                continue;
            }
            if (farmDao.getFarmZone(dto.getFarmZoneCode()) != null) {
                errList.add(address + "圈舍编号已存在");
                continue;
            }
            dto.setCreateUser(username);
            dto.setUpdateUser(username);
            dto.setFarmId(farmDTO.getFarmId());
            if (farmDao.addFarmZone(dto) == 0) {
                errList.add(address + "添加失败");
            }
        }
        return errList;
    }

    private void setCurrentSize(List<FarmZoneDTO> list) {
        if (list.size() > 0) {
            List<String> farmZoneCodeList = list.stream().map(FarmZoneDTO::getFarmZoneCode).toList();
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCodeList(farmZoneCodeList);
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            for (FarmZoneDTO dto : list) {
                dto.setCurrentSize((int) cattleList.stream().filter(item -> dto.getFarmZoneCode().equals(item.getFarmZoneCode())).count());
            }
        }
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
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
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
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
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
