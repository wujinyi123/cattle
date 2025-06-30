package com.manage.cattle.service.bread.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.breed.BreedDao;
import com.manage.cattle.dto.base.CattleDTO;
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
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    @Override
    public PageInfo<BreedRegisterDTO> pageBreedRegister(BreedRegisterQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedRegister(qo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedRegister(BreedRegisterDTO dto) {
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (Objects.isNull(cattleDTO)) {
            throw new BusinessException("牛只不存在");
        }
        if (!"female".equals(cattleDTO.getSex())) {
            throw new BusinessException("牛只不是雌性");
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
        return breedDao.addBreedRegister(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedRegister(List<Integer> ids) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return breedDao.delBreedRegister(ids);
        }
        String username = JWTUtil.getUsername();
        List<BreedRegisterDTO> breedRegisterList = breedDao.listBreedRegisterByIds(ids).stream().filter(item -> {
            Set<String> userSet = new HashSet<>();
            userSet.addAll(CommonUtil.stringToList(item.getFarmAdmin()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmOwner()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmEmployee()));
            return !userSet.contains(username);
        }).toList();
        if (breedRegisterList.size() > 0) {
            throw new BusinessException("部分权限不足");
        }
        return breedDao.delBreedRegister(ids);
    }

    @Override
    public PageInfo<BreedPregnancyCheckDTO> pageBreedPregnancyCheck(BreedPregnancyCheckQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedPregnancyCheck(qo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto) {
        BreedRegisterDTO BreedRegisterDTO = breedDao.getBreedRegister(dto.getRegisterId());
        if (Objects.isNull(BreedRegisterDTO)) {
            throw new BusinessException("登记号不存在");
        }
        Set<String> userSet = new HashSet<>();
        userSet.addAll(CommonUtil.stringToList(BreedRegisterDTO.getFarmOwner()));
        userSet.addAll(CommonUtil.stringToList(BreedRegisterDTO.getFarmEmployee()));
        userSet.addAll(CommonUtil.stringToList(BreedRegisterDTO.getFarmAdmin()));
        String username = JWTUtil.getUsername();
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !userSet.contains(username)) {
            throw new BusinessException("权限不足");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBreedPregnancyCheck(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedPregnancyCheck(List<Integer> ids) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return breedDao.delBreedPregnancyCheck(ids);
        }
        String username = JWTUtil.getUsername();
        List<BreedPregnancyCheckDTO> breedPregnancyCheckList = breedDao.listBreedPregnancyCheckByIds(ids).stream().filter(item -> {
            Set<String> userSet = new HashSet<>();
            userSet.addAll(CommonUtil.stringToList(item.getFarmOwner()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmAdmin()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmEmployee()));
            return !userSet.contains(username);
        }).toList();
        if (breedPregnancyCheckList.size() > 0) {
            throw new BusinessException("部分权限不足");
        }
        return breedDao.delBreedPregnancyCheck(ids);
    }

    @Override
    public PageInfo<BreedPregnancyResultDTO> pageBreedPregnancyResult(BreedPregnancyResultQO qo) {
        PageHelper.startPage(qo);
        PageInfo<BreedPregnancyResultDTO> pageInfo = new PageInfo<>(breedDao.listBreedPregnancyResult(qo));
        Set<String> childrenIds = new HashSet<>();
        pageInfo.getList().forEach(dto -> childrenIds.addAll(CommonUtil.stringToList(dto.getChildrenIds())));
        List<CattleDTO> cattleList = new ArrayList<>();
        if (childrenIds.size() > 0) {
            CattleQO cattleQO = new CattleQO();
            cattleQO.setCattleIds(new ArrayList<>(childrenIds));
            cattleList = cattleDao.listCattle(cattleQO);
        }
        for (BreedPregnancyResultDTO dto : pageInfo.getList()) {
            List<String> cattleIds = CommonUtil.stringToList(dto.getChildrenIds());
            dto.setChildren(cattleList.stream().filter(item -> cattleIds.contains(item.getCattleId())).toList());
        }
        return pageInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyResult(BreedPregnancyResultDTO dto) {
        BreedRegisterDTO BreedRegisterDTO = breedDao.getBreedRegister(dto.getRegisterId());
        if (Objects.isNull(BreedRegisterDTO)) {
            throw new BusinessException("登记号不存在");
        }
        Set<String> userSet = new HashSet<>();
        userSet.addAll(CommonUtil.stringToList(BreedRegisterDTO.getFarmOwner()));
        userSet.addAll(CommonUtil.stringToList(BreedRegisterDTO.getFarmEmployee()));
        userSet.addAll(CommonUtil.stringToList(BreedRegisterDTO.getFarmAdmin()));
        String username = JWTUtil.getUsername();
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !userSet.contains(username)) {
            throw new BusinessException("权限不足");
        }
        if ("birth".equals(dto.getResult()) && CollectionUtils.isEmpty(dto.getChildren())) {
            throw new BusinessException("生育时，后代不能为空");
        }
        if ("abortion".equals(dto.getResult()) && !CollectionUtils.isEmpty(dto.getChildren())) {
            throw new BusinessException("流产时，后代应为空");
        }
        if (!CollectionUtils.isEmpty(dto.getChildren())) {
            List<String> codes = dto.getChildren().stream().map(CattleDTO::getCattleCode).toList();
            CattleQO qo = new CattleQO();
            qo.setCattleCodes(codes);
            List<CattleDTO> cattleList = cattleDao.listCattle(qo);
            if (cattleList.size() > 0) {
                throw new BusinessException("牛只耳牌号("
                        + cattleList.stream()
                        .map(CattleDTO::getCattleCode)
                        .collect(Collectors.joining(","))
                        + ")已存在");
            }
            for (CattleDTO cattleDTO : dto.getChildren()) {
                cattleDTO.setCattleId(IdUtil.getSnowflakeNextIdStr());
                cattleDTO.setCreateUser(username);
                cattleDTO.setUpdateUser(username);
                if (cattleDao.addCattle(cattleDTO) == 0) {
                    throw new BusinessException("添加牛只失败：" + cattleDTO.getCattleCode());
                }
            }
            dto.setChildrenIds(dto.getChildren().stream().map(CattleDTO::getCattleId).collect(Collectors.joining(",")));
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBreedPregnancyResult(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedPregnancyResult(List<Integer> ids) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return breedDao.delBreedPregnancyResult(ids);
        }
        String username = JWTUtil.getUsername();
        List<BreedPregnancyResultDTO> breedPregnancyResultList = breedDao.listBreedPregnancyResultByIds(ids).stream().filter(item -> {
            Set<String> userSet = new HashSet<>();
            userSet.addAll(CommonUtil.stringToList(item.getFarmOwner()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmAdmin()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmEmployee()));
            return !userSet.contains(username);
        }).toList();
        if (breedPregnancyResultList.size() > 0) {
            throw new BusinessException("部分权限不足");
        }
        return breedDao.delBreedPregnancyResult(ids);
    }
}
