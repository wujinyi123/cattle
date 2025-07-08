package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.common.CommonDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.CattleService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
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
    private CommonDao commonDao;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> importCattle(String requireFields, List<CattleDTO> list) {
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
        String[] requireFieldArr = requireFields.split(",");
        SysConfigQO qo = new SysConfigQO();
        qo.setCodeList(Arrays.asList("cattleBreed", "cattleSex"));
        Map<String, String> codeMap = commonDao.listSysConfig(qo).stream().collect(Collectors.toMap(dto -> dto.getCode() + "#" + dto.getValue(),
                SysConfigDTO::getKey, (key1, key2) -> key2));
        List<String> errList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CattleDTO dto = list.get(i);
            String address = "第" + (i + 2) + "行：";
            if (!CommonUtil.checkRequire(requireFieldArr, dto)) {
                errList.add(address + "必填项不能为空");
                continue;
            }
            dto.setBreed(codeMap.get("cattleBreed#" + dto.getBreedValue()));
            dto.setSex(codeMap.get("cattleSex#" + dto.getSexValue()));
            if (StrUtil.isBlank(dto.getBreed()) || StrUtil.isBlank(dto.getSex())) {
                errList.add(address + "品种或性别错误");
                continue;
            }
            if (CommonUtil.strToDate(dto.getBirthday()) == null) {
                errList.add(address + "出生日期格式错误，请使用yyyy-MM-dd格式");
                continue;
            }
            FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
            if (!"Y".equals(isSysAdmin) && !username.equals(farmZoneDTO.getFarmOwner()) && !CommonUtil.stringToList(farmZoneDTO.getFarmAdmin()).contains(username)) {
                errList.add(address + "权限不足");
                continue;
            }
            if (cattleDao.getCattle(dto.getCattleCode()) != null) {
                errList.add(address + "耳牌号已存在");
                continue;
            }
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(farmZoneDTO.getFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZoneDTO.getSize() <= cattleList.size()) {
                errList.add(address + "圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
                continue;
            }
            dto.setCreateUser(username);
            dto.setUpdateUser(username);
            if (cattleDao.addCattle(dto) == 0) {
                errList.add(address + "添加失败");
            }
        }
        return errList;
    }

    @Override
    public CattleDTO getCattle(String cattleCode) {
        CattleDTO dto = cattleDao.getCattle(cattleCode);
        setAge(dto);
        return dto;
    }

    private void setAge(CattleDTO dto) {
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
        FarmDTO farmDTO = farmDao.getFarmById(dto.getFarmId());
        if (Objects.isNull(farmDTO)) {
            throw new BusinessException("牧场不存在");
        }
        List<String> adminList = CommonUtil.stringToList(farmDTO.getAdmin());
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
        if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner()) && !adminList.contains(username)) {
            throw new BusinessException("权限不足");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if ("add".equals(type)) {
            if (Objects.nonNull(cattleDao.getCattle(dto.getCattleCode()))) {
                throw new BusinessException("耳牌号已存在");
            }
            FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(farmZoneDTO.getFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZoneDTO.getSize() <= cattleList.size()) {
                throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
            }
            return cattleDao.addCattle(dto);
        } else {
            return cattleDao.updateCattle(dto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delCattle(List<String> cattleCodeList) {
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return cattleDao.delCattle(cattleCodeList);
        }
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(cattleCodeList);
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        String username = UserUtil.getUsername();
        if (cattleList.stream().anyMatch(item -> !username.equals(item.getFarmAdmin()) && !CommonUtil.stringToList(item.getFarmAdmin()).contains(username))) {
            throw new BusinessException("权限不足");
        }
        return cattleDao.delCattle(cattleCodeList);
    }
}
