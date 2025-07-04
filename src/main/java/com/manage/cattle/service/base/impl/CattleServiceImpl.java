package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.service.base.CattleService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.JWTUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CattleServiceImpl implements CattleService {
    @Resource
    private CattleDao cattleDao;

    @Resource
    private FarmDao farmDao;

    @Override
    public PageInfo<CattleDTO> pageCattle(CattleQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(cattleDao.listCattle(qo));
    }

    @Override
    public CattleDTO getCattle(String cattleCode) {
        return cattleDao.getCattle(cattleCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveCattle(String type, CattleDTO dto) {
        FarmDTO farmDTO = farmDao.getFarmById(dto.getFarmId());
        if (Objects.isNull(farmDTO)) {
            throw new BusinessException("牧场不存在");
        }
        List<String> adminList = CommonUtil.stringToList(farmDTO.getAdmin());
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        String username = JWTUtil.getUsername();
        if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner()) && !adminList.contains(username)) {
            throw new BusinessException("权限不足");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if ("add".equals(type)) {
            if (Objects.nonNull(cattleDao.getCattle(dto.getCattleCode()))) {
                throw new BusinessException("耳牌号已存在");
            }
            return cattleDao.addCattle(dto);
        } else {
            return cattleDao.updateCattle(dto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delCattle(List<String> cattleCodeList) {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return cattleDao.delCattle(cattleCodeList);
        }
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(cattleCodeList);
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        String username = JWTUtil.getUsername();
        if (cattleList.stream().anyMatch(item -> !username.equals(item.getFarmAdmin()) && !CommonUtil.stringToList(item.getFarmAdmin()).contains(username))) {
            throw new BusinessException("权限不足");
        }
        return cattleDao.delCattle(cattleCodeList);
    }
}
