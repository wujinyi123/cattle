package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.service.base.FarmService;
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FarmServiceImpl implements FarmService {
    @Resource
    private FarmDao farmDao;

    @Override
    public PageInfo<FarmDTO> pageFarm(FarmQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(farmDao.listFarm(qo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveFarm(String type, FarmDTO dto) {
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
        return farmDao.delFarm(farmIds);
    }
}
