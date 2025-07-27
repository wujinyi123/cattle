package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.FarmZoneQO;
import com.manage.cattle.service.base.FarmService;
import com.manage.cattle.util.UserUtil;
import com.manage.cattle.util.PermissionUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FarmServiceImpl implements FarmService {
    @Resource
    private FarmDao farmDao;

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

    @Override
    public FarmDTO getFarm(String farmCode) {
        return farmDao.getFarm(farmCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveFarm(String type, FarmDTO dto) {
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getPayloadVal("username");
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        List<FarmDTO> existFarmList = farmDao.listFarm(new FarmQO());
        List<FarmDTO> farmList = "add".equals(type) ?
                existFarmList.stream().filter(item -> item.getFarmCode().equals(dto.getFarmCode()) || item.getFarmName().equals(dto.getFarmName())).toList() :
                existFarmList.stream().filter(item -> !item.getFarmCode().equals(dto.getFarmCode()) && item.getFarmName().equals(dto.getFarmName())).toList();
        if (farmList.size() > 0) {
            throw new BusinessException("牧场代码或名称已存在");
        }
        int result = "add".equals(type) ? farmDao.addFarm(dto) : farmDao.updateFarm(dto);
        if (result == 0) {
            throw new BusinessException("保存失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delFarm(List<String> farmCodeList) {
        PermissionUtil.onlySysAdmin();
        FarmZoneQO qo = new FarmZoneQO();
        qo.setFarmCodeList(farmCodeList);
        List<String> farmList = farmDao.listFarmZone(qo).stream().map(FarmZoneDTO::getFarmName).toList();
        if (farmList.size() > 0) {
            throw new BusinessException("牧场(" + String.join(",", farmList) + ")中存在圈舍，请先删除圈舍信息");
        }
        return farmDao.delFarm(farmCodeList);
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
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getPayloadVal("username");
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        FarmDTO farmDTO = farmDao.getFarm(dto.getFarmCode());
        if (Objects.isNull(farmDTO)) {
            throw new BusinessException("牧场不存在");
        }
        FarmZoneQO qo = new FarmZoneQO();
        qo.setFarmCode(dto.getFarmCode());
        List<FarmZoneDTO> farmZoneList = farmDao.listFarmZone(qo);
        long count;
        if ("add".equals(type)) {
            count = farmZoneList.stream()
                    .filter(item -> item.getFarmZoneName().equals(dto.getFarmZoneName()))
                    .count();
        } else {
            count = farmZoneList.stream()
                    .filter(item -> !item.getFarmZoneCode().equals(dto.getFarmZoneCode()) && item.getFarmZoneName().equals(dto.getFarmZoneName()))
                    .count();
        }
        if (count > 0L) {
            throw new BusinessException("圈舍名称已存在");
        }
        if ("add".equals(type) && farmDao.getFarmZone(dto.getFarmZoneCode()) != null) {
            throw new BusinessException("圈舍编号已存在");
        }
        int result = "add".equals(type) ? farmDao.addFarmZone(dto) : farmDao.updateFarmZone(dto);
        if (result == 0) {
            throw new BusinessException("保存失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delFarmZone(List<String> farmZoneCodeList) {
        PermissionUtil.onlySysAdmin();
        CattleQO qo = new CattleQO();
        qo.setFarmZoneCodeList(farmZoneCodeList);
        List<String> farmList = cattleDao.listCattle(qo).stream().map(CattleDTO::getFarmZoneCode).toList();
        if (farmList.size() > 0) {
            throw new BusinessException("圈舍编号(" + String.join(",", farmList) + ")中存在牛只，请先删除牛只信息");
        }
        return farmDao.delFarmZone(farmZoneCodeList);
    }
}
