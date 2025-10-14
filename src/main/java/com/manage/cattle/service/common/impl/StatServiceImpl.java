package com.manage.cattle.service.common.impl;

import cn.hutool.core.util.StrUtil;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.common.HomeStat;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.service.common.StatService;
import com.manage.cattle.util.CommonUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {
    @Resource
    private CattleDao cattleDao;

    @Override
    public HomeStat homeStat(String farmCode) {
        List<CattleDTO> cattleList = cattleDao.listCattle(new CattleQO());
        cattleList.forEach(this::setYearMonthDay);
        HomeStat homeStat = new HomeStat();
        homeStat.setCattleTypeList(statCattleType(cattleList));
        homeStat.setCurrentFarmCattleTypeList(statCattleType(cattleList.stream().filter(item -> farmCode.equals(item.getFarmCode())).toList()));
        Map<String, List<CattleDTO>> groupByFarm = cattleList.stream().collect(Collectors.groupingBy(CattleDTO::getFarmName));
        homeStat.setFarmCattleList(statCattle(groupByFarm));
        List<CattleDTO> currentFarmCattleList = cattleList.stream().filter(item -> item.getFarmCode().equals(farmCode)).toList();
        Map<String, List<CattleDTO>> groupByFarmZone = currentFarmCattleList.stream().collect(Collectors.groupingBy(CattleDTO::getFarmZoneCode));
        homeStat.setFarmZoneCattleList(statCattle(groupByFarmZone));
        return homeStat;
    }

    private void setYearMonthDay(CattleDTO dto) {
        if (dto == null) {
            return;
        }
        String start = dto.getBirthday();
        String end = CommonUtil.dateToStr(new Date());
        if (StrUtil.equals(start, end)) {
            dto.setYear(0);
            dto.setMonth(0);
            dto.setDay(0);
            return;
        }
        // 将字符串转换为LocalDate对象
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        // 计算两个日期之间的差异
        Period period = Period.between(startDate, endDate);
        dto.setYear(period.getYears());
        dto.setMonth(period.getMonths());
        dto.setDay(period.getDays());
    }

    private List<HomeStat.Node> statCattleType(List<CattleDTO> cattleList) {
        List<HomeStat.Node> statCattle = new ArrayList<>();
        int calfMale = 0;
        int calfFemale = 0;
        int weanedCalfMale = 0;
        int weanedCalfFemale = 0;
        int youngMale = 0;
        int youngFemale = 0;
        int deliveredCowMale = 0;
        int deliveredCowFemale = 0;
        for (CattleDTO dto : cattleList) {
            int year = dto.getYear();
            int month = dto.getMonth();
            if (year > 0) {
                if ("公".equals(dto.getSex())) {
                    deliveredCowMale++;
                } else {
                    deliveredCowFemale++;
                }
                continue;
            }
            if (month >= 6 && month < 12) {
                if ("公".equals(dto.getSex())) {
                    youngMale++;
                } else {
                    youngFemale++;
                }
                continue;
            }
            if (month >= 4 && month < 6) {
                if ("公".equals(dto.getSex())) {
                    weanedCalfMale++;
                } else {
                    weanedCalfFemale++;
                }
                continue;
            }
            if ("公".equals(dto.getSex())) {
                calfMale++;
            } else {
                calfFemale++;
            }
        }
        statCattle.add(new HomeStat.Node("0-4月公犊牛", calfMale));
        statCattle.add(new HomeStat.Node("0-4月母犊牛", calfFemale));
        statCattle.add(new HomeStat.Node("4-6月断奶公犊牛", weanedCalfMale));
        statCattle.add(new HomeStat.Node("4-6月断奶母犊牛", weanedCalfFemale));
        statCattle.add(new HomeStat.Node("6-12月育成公牛", youngMale));
        statCattle.add(new HomeStat.Node("6-12月育成母牛", youngFemale));
        statCattle.add(new HomeStat.Node("12月以上育肥公牛", deliveredCowMale));
        statCattle.add(new HomeStat.Node("12月以上经产母牛", deliveredCowFemale));
        return statCattle;
    }

    private List<HomeStat.Node> statCattle(Map<String, List<CattleDTO>> groupBy) {
        List<HomeStat.Node> statCattle = new ArrayList<>();
        for (Map.Entry<String, List<CattleDTO>> entry : groupBy.entrySet()) {
            HomeStat.Node node = new HomeStat.Node();
            node.setLabel(entry.getKey());
            node.setIntValue(entry.getValue().size());
            statCattle.add(node);
        }
        statCattle.sort((a, b) -> b.getIntValue() - a.getIntValue());
        return statCattle.size() > 10 ? statCattle.subList(0, 10) : statCattle;
    }
}
