package com.manage.cattle.service.bread;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.breed.BreedDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class BreedServiceImpl implements BreedService {
    @Resource
    private BreedDao breedDao;

    @Resource
    private CattleDao cattleDao;

    @Override
    public PageInfo<BreedRegisterDTO> pageBeedRegister(BreedRegisterQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBeedRegister(qo));
    }

    @Override
    public int addBeedRegister(BreedRegisterDTO dto) {
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (Objects.isNull(cattleDTO)) {
            throw new BusinessException("牛只不存在");
        }
        dto.setCattleId(cattleDTO.getCattleId());
        Set<String> userSet = new HashSet<>();
        userSet.addAll(CommonUtil.stringToList(cattleDTO.getFarmOwner()));
        userSet.addAll(CommonUtil.stringToList(cattleDTO.getFarmAdmin()));
        userSet.addAll(CommonUtil.stringToList(cattleDTO.getFarmEmployee()));
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        String username = JWTUtil.getUsername();
        if (!"Y".equals(isSysAdmin) && !userSet.contains(username)) {
            throw new BusinessException("权限不足");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        dto.setRegisterId(IdUtil.getSnowflakeNextIdStr());
        return breedDao.addBeedRegister(dto);
    }

    @Override
    public int delBeedRegister(List<Integer> ids) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return breedDao.delBeedRegister(ids);
        }
        String username = JWTUtil.getUsername();
        List<BreedRegisterDTO> breedRegisterList = breedDao.listBeedRegisterByIds(ids).stream().filter(item -> {
            Set<String> userSet = new HashSet<>();
            userSet.addAll(CommonUtil.stringToList(item.getFarmAdmin()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmOwner()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmEmployee()));
            return !userSet.contains(username);
        }).toList();
        if (breedRegisterList.size() > 0) {
            throw new BusinessException("部分权限不足");
        }
        return breedDao.delBeedRegister(ids);
    }

    @Override
    public PageInfo<BreedPregnancyCheckDTO> pageBeedPregnancyCheck(BreedPregnancyCheckQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBeedPregnancyCheck(qo));
    }

    @Override
    public int addBeedPregnancyCheck(BreedPregnancyCheckDTO dto) {
        BreedRegisterDTO beedRegisterDTO = breedDao.getBeedRegister(dto.getRegisterId());
        if (Objects.isNull(beedRegisterDTO)) {
            throw new BusinessException("登记号不存在");
        }
        Set<String> userSet = new HashSet<>();
        userSet.addAll(CommonUtil.stringToList(beedRegisterDTO.getFarmOwner()));
        userSet.addAll(CommonUtil.stringToList(beedRegisterDTO.getFarmEmployee()));
        userSet.addAll(CommonUtil.stringToList(beedRegisterDTO.getFarmAdmin()));
        String username = JWTUtil.getUsername();
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !userSet.contains(username)) {
            throw new BusinessException("权限不足");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBeedPregnancyCheck(dto);
    }

    @Override
    public int delBeedPregnancyCheck(List<Integer> ids) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return breedDao.delBeedPregnancyCheck(ids);
        }
        String username = JWTUtil.getUsername();
        List<BreedPregnancyCheckDTO> breedPregnancyCheckList = breedDao.listBeedPregnancyCheckByIds(ids).stream().filter(item -> {
            Set<String> userSet = new HashSet<>();
            userSet.addAll(CommonUtil.stringToList(item.getFarmOwner()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmAdmin()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmEmployee()));
            return !userSet.contains(username);
        }).toList();
        if (breedPregnancyCheckList.size() > 0) {
            throw new BusinessException("部分权限不足");
        }
        return breedDao.delBeedPregnancyCheck(ids);
    }
}
