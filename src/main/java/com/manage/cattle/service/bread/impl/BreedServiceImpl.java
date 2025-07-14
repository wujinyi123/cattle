package com.manage.cattle.service.bread.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.breed.BreedDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import com.manage.cattle.service.bread.BreedService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BreedServiceImpl implements BreedService {
    @Resource
    private BreedDao breedDao;

    @Resource
    private CattleDao cattleDao;

    @Resource
    private FarmDao farmDao;

    @Override
    public PageInfo<BreedRegisterDTO> pageBreedRegister(BreedRegisterQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedRegister(qo));
    }

    @Override
    public List<BreedRegisterDTO> listBreedRegister(BreedRegisterQO qo) {
        return breedDao.listBreedRegister(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedRegister(BreedRegisterDTO dto) {
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (Objects.isNull(cattleDTO)) {
            throw new BusinessException("牛只不存在");
        }
        if (!"母".equals(cattleDTO.getSex())) {
            throw new BusinessException("牛只不是雌性");
        }
        if (!StrUtil.equals(dto.getFarmCode(), cattleDTO.getFarmCode())) {
            throw new BusinessException("请输入当前牧场牛只耳牌号");
        }
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        dto.setRegisterId(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        return breedDao.addBreedRegister(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedRegister(List<Integer> ids) {
        List<BreedRegisterDTO> breedRegisterList = breedDao.listBreedRegisterByIds(ids);
        if (breedRegisterList.size() == 0) {
            throw new BusinessException("查无数据");
        }
        List<String> registerIds = breedRegisterList.stream().map(BreedRegisterDTO::getRegisterId).toList();
        int result = breedDao.delBreedRegister(ids);
        breedDao.delBreedPregnancyCheckByRegisterId(registerIds);
        breedDao.delBreedPregnancyResultByRegisterId(registerIds);
        return result;
    }

    @Override
    public PageInfo<BreedPregnancyCheckDTO> pageBreedPregnancyCheck(BreedPregnancyCheckQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedPregnancyCheck(qo));
    }

    @Override
    public List<BreedPregnancyCheckDTO> listBreedPregnancyCheck(BreedPregnancyCheckQO qo) {
        return breedDao.listBreedPregnancyCheck(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto) {
        BreedRegisterDTO breedRegisterDTO = breedDao.getBreedRegister(dto.getRegisterId());
        if (!StrUtil.equals(dto.getFarmCode(), breedRegisterDTO.getFarmCode())) {
            throw new BusinessException("请输入当前牧场的登记号");
        }
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBreedPregnancyCheck(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedPregnancyCheck(List<Integer> ids) {
        return breedDao.delBreedPregnancyCheck(ids);
    }

    @Override
    public PageInfo<BreedPregnancyResultDTO> pageBreedPregnancyResult(BreedPregnancyResultQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedPregnancyResult(qo));
    }

    @Override
    public List<BreedPregnancyResultDTO> listBreedPregnancyResult(BreedPregnancyResultQO qo) {
        return breedDao.listBreedPregnancyResult(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyResult(BreedPregnancyResultDTO dto) {
        BreedRegisterDTO breedRegisterDTO = breedDao.getBreedRegister(dto.getRegisterId());
        if (!StrUtil.equals(dto.getFarmCode(), breedRegisterDTO.getFarmCode())) {
            throw new BusinessException("请输入当前牧场的登记号");
        }
        if (StrUtil.isNotBlank(dto.getChildCattleCode()) && cattleDao.getCattle(dto.getChildCattleCode()) != null) {
            throw new BusinessException("牛犊子耳牌号已存在");
        }
        String username = UserUtil.getUsername();
        CattleDTO cattleDTO = new CattleDTO();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        cattleDTO.setFarmZoneCode(dto.getChildFarmZoneCode());
        cattleDTO.setCattleCode(dto.getChildCattleCode());
        cattleDTO.setBreed(dto.getBreed());
        cattleDTO.setSex(dto.getSex());
        cattleDTO.setColor(dto.getColor());
        cattleDTO.setBirthday(dto.getResultDay());
        if (cattleDao.addCattle(cattleDTO) == 0) {
            throw new BusinessException("添加牛犊子失败");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        int res = breedDao.addBreedPregnancyResult(dto);
        if (res == 0) {
            throw new BusinessException("添加失败");
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedPregnancyResult(List<Integer> ids) {
        return breedDao.delBreedPregnancyResult(ids);
    }
}
