package com.manage.cattle.service.healthDisease.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.healthDisease.HealthDiseaseDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.healthDisease.ImmunityRegisterDTO;
import com.manage.cattle.dto.healthDisease.QuarantineRegisterDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.healthDisease.ImmunityRegisterQO;
import com.manage.cattle.qo.healthDisease.QuarantineRegisterQO;
import com.manage.cattle.service.healthDisease.HealthDiseaseService;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthDiseaseServiceImpl implements HealthDiseaseService {
    @Resource
    private HealthDiseaseDao healthDiseaseDao;

    @Resource
    private CattleDao cattleDao;

    @Override
    public PageInfo<QuarantineRegisterDTO> pageQuarantineRegister(QuarantineRegisterQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(healthDiseaseDao.listQuarantineRegister(qo));
    }

    @Override
    public int addQuarantineRegister(QuarantineRegisterDTO dto) {
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> existCode = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        List<String> errorCattleCode = dto.getCattleCodeList().stream().filter(item->!existCode.contains(item)).toList();
        if (errorCattleCode.size() > 0) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCattleCode) + ")不存在");
        }
        errorCattleCode = cattleList.stream()
                .filter(item -> !StrUtil.equals(item.getFarmCode(), dto.getFarmCode()))
                .map(CattleDTO::getCattleCode)
                .toList();
        if (errorCattleCode.size() > 0) {
            throw new BusinessException("请选择当前牧场牛只，耳牌号(" + String.join(",", errorCattleCode) + ")错误");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return healthDiseaseDao.addQuarantineRegister(dto);
    }

    @Override
    public int delQuarantineRegister(List<Integer> ids) {
        return healthDiseaseDao.delQuarantineRegister(ids);
    }

    @Override
    public PageInfo<ImmunityRegisterDTO> pageImmunityRegister(ImmunityRegisterQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(healthDiseaseDao.listImmunityRegister(qo));
    }

    @Override
    public int addImmunityRegister(ImmunityRegisterDTO dto) {
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> existCode = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        List<String> errorCattleCode = dto.getCattleCodeList().stream().filter(item->!existCode.contains(item)).toList();
        if (errorCattleCode.size() > 0) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCattleCode) + ")不存在");
        }
        errorCattleCode = cattleList.stream()
                .filter(item -> !StrUtil.equals(item.getFarmCode(), dto.getFarmCode()))
                .map(CattleDTO::getCattleCode)
                .toList();
        if (errorCattleCode.size() > 0) {
            throw new BusinessException("请选择当前牧场牛只，耳牌号(" + String.join(",", errorCattleCode) + ")错误");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return healthDiseaseDao.addImmunityRegister(dto);
    }

    @Override
    public int delImmunityRegister(List<Integer> ids) {
        return healthDiseaseDao.delImmunityRegister(ids);
    }
}
