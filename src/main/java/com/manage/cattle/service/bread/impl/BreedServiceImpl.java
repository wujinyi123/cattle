package com.manage.cattle.service.bread.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.breed.BreedDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.breed.BreedBaseDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import com.manage.cattle.service.bread.BreedService;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
        if (CollectionUtils.isEmpty(dto.getCattleCodeList())) {
            throw new BusinessException("牛只耳牌号不能为空");
        }
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> existCattleCode = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        String notExist = dto.getCattleCodeList().stream().filter(item -> !existCattleCode.contains(item)).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(notExist)) {
            throw new BusinessException("牛只(" + notExist + ")不存在");
        }
        String sexErr = cattleList.stream().filter(item -> !"母".equals(item.getSex())).map(CattleDTO::getCattleCode).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(sexErr)) {
            throw new BusinessException("牛只(" + sexErr + ")不是母的");
        }
        String farmErr =
                cattleList.stream().filter(item -> !StrUtil.equals(dto.getFarmCode(), item.getFarmCode())).map(CattleDTO::getCattleCode).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(farmErr)) {
            throw new BusinessException("牛只(" + farmErr + ")不是当前牧场的");
        }
        List<String> breedingCattle = breedDao.listBreedingCattleCode(dto.getCattleCodeList()).stream().map(BreedRegisterDTO::getCattleCode).toList();
        if (!CollectionUtils.isEmpty(breedingCattle)) {
            throw new BusinessException("牛只(" + String.join(",", breedingCattle) + ")正在妊娠中");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.batchAddBreedRegister(dto);
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
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBreedPregnancyCheck(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyCheckByCattle(BreedPregnancyCheckDTO dto) {
        if (CollectionUtils.isEmpty(dto.getCattleCodeList())) {
            throw new BusinessException("牛只耳牌号不能为空");
        }
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> existCattleCode = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        String notExist = dto.getCattleCodeList().stream().filter(item -> !existCattleCode.contains(item)).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(notExist)) {
            throw new BusinessException("牛只(" + notExist + ")不存在");
        }
        String sexErr = cattleList.stream().filter(item -> !"母".equals(item.getSex())).map(CattleDTO::getCattleCode).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(sexErr)) {
            throw new BusinessException("牛只(" + sexErr + ")不是母的");
        }
        String farmErr =
                cattleList.stream().filter(item -> !StrUtil.equals(dto.getFarmCode(), item.getFarmCode())).map(CattleDTO::getCattleCode).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(farmErr)) {
            throw new BusinessException("牛只(" + farmErr + ")不是当前牧场的");
        }
        List<BreedRegisterDTO> breedingCattle = breedDao.listBreedingCattleCode(dto.getCattleCodeList());
        List<String> breedingCattleCode = breedingCattle.stream().map(BreedRegisterDTO::getCattleCode).toList();
        List<String> noBreedingCattleCode = dto.getCattleCodeList().stream().filter(item -> !breedingCattleCode.contains(item)).toList();
        if (!CollectionUtils.isEmpty(noBreedingCattleCode)) {
            throw new BusinessException("牛只(" + String.join(",", noBreedingCattleCode) + ")不是正在妊娠中");
        }
        dto.setRegisterIds(breedingCattle.stream().filter(item -> dto.getCattleCodeList().contains(item.getCattleCode())).map(BreedBaseDTO::getRegisterId).toList());
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.batchAddBreedPregnancyCheck(dto);
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
        if (breedRegisterDTO == null) {
            throw new BusinessException("登记号不正确");
        }
        if (!StrUtil.equals(dto.getFarmCode(), breedRegisterDTO.getFarmCode())) {
            throw new BusinessException("请输入当前牧场的登记号");
        }
        if (StrUtil.isNotBlank(dto.getChildCattleCode()) && cattleDao.getCattle(dto.getChildCattleCode()) != null) {
            throw new BusinessException("牛犊子耳牌号已存在");
        }
        String username = UserUtil.getCurrentUsername();
        if (!"死胎".equals(dto.getResult())) {
            FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getChildFarmZoneCode());
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(dto.getFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZoneDTO.getSize() <= cattleList.size()) {
                throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
            }
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
