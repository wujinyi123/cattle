package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.SysDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.FarmZoneQO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.CattleService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.PermissionUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CattleServiceImpl implements CattleService {
    @Resource
    private CattleDao cattleDao;

    @Resource
    private FarmDao farmDao;

    @Resource
    private SysDao sysDao;

    @Override
    public PageInfo<CattleDTO> pageCattle(CattleQO qo) {
        PageHelper.startPage(qo);
        PageInfo<CattleDTO> pageInfo = new PageInfo<>(cattleDao.listCattle(qo));
        pageInfo.getList().forEach(this::setAge);
        return pageInfo;
    }

    @Override
    public List<CattleDTO> listCattle(CattleQO qo) {
        List<CattleDTO> list = cattleDao.listCattle(qo);
        list.forEach(this::setAge);
        return list;
    }

    @Override
    public CattleDTO getCattle(String cattleCode) {
        CattleDTO dto = cattleDao.getCattle(cattleCode);
        if (dto == null) {
            throw new BusinessException("牛只不存在");
        }
        setAge(dto);
        return dto;
    }

    private void setAge(CattleDTO dto) {
        if (dto == null) {
            return;
        }
        String start = dto.getBirthday();
        String end = CommonUtil.dateToStr(new Date());
        if (StrUtil.equals(start, end)) {
            dto.setAge("0天");
            return;
        }
        // 将字符串转换为LocalDate对象
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        // 计算两个日期之间的差异
        Period period = Period.between(startDate, endDate);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        String age = "";
        if (years != 0) {
            age += years + "年";
        }
        if (months != 0) {
            age += months + "月";
        }
        if (days != 0) {
            age += days + "天";
        }
        dto.setAge(age);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveCattle(String type, CattleDTO dto) {
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getPayloadVal("username");
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
        if (Objects.isNull(farmZoneDTO)) {
            throw new BusinessException("圈舍编号不存在");
        }
        if ("add".equals(type)) {
            if (cattleDao.getCattle(dto.getCattleCode()) != null) {
                throw new BusinessException("耳牌号已存在");
            } else {
                CattleQO cattleQO = new CattleQO();
                cattleQO.setFarmZoneCode(farmZoneDTO.getFarmZoneCode());
                List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
                if (farmZoneDTO.getSize() <= cattleList.size()) {
                    throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
                }
            }
        }
        int result = "add".equals(type) ? cattleDao.addCattle(dto) : cattleDao.updateCattle(dto);
        if (result == 0) {
            throw new BusinessException("保存失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delCattle(List<String> cattleCodeList) {
        PermissionUtil.onlySysAdmin();
        return cattleDao.delCattle(cattleCodeList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> importCattle(List<CattleDTO> importList) {
        FarmZoneQO farmZoneQO = new FarmZoneQO();
        farmZoneQO.setFarmCode(importList.get(0).getFarmCode());
        List<String> farmZoneCodeList = farmDao.listFarmZone(farmZoneQO).stream().map(FarmZoneDTO::getFarmZoneCode).toList();
        SysConfigQO sysConfigQO = new SysConfigQO();
        sysConfigQO.setCode("cattleBreed");
        Map<String, String> breedMap = sysDao.listSysConfig(sysConfigQO).stream().collect(Collectors.toMap(SysConfigDTO::getValue,
                SysConfigDTO::getKey));
        List<String> errorList = new ArrayList<>();
        for (CattleDTO dto : importList) {
            if (StrUtil.isNotBlank(dto.getImportError())) {
                errorList.add(dto.getImportError());
                continue;
            }
            if (!farmZoneCodeList.contains(dto.getFarmZoneCode())) {
                errorList.add("圈舍编号不正确");
                continue;
            }
            if (cattleDao.getCattle(dto.getCattleCode()) != null) {
                errorList.add("耳牌号已存在");
                continue;
            }
            dto.setBreed(breedMap.get(dto.getBreedValue()));
            if (StrUtil.isBlank(dto.getBreed())) {
                errorList.add("品种不正确");
                continue;
            }
            int res = cattleDao.addCattle(dto);
            if (res == 0) {
                errorList.add("添加失败");
            }
        }
        return errorList;
    }
}
