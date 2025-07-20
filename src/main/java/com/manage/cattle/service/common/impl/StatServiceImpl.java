package com.manage.cattle.service.common.impl;

import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.common.HomeStat;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.service.common.StatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        Map<String, List<CattleDTO>> groupByFarm = cattleList.stream().collect(Collectors.groupingBy(CattleDTO::getFarmName));
        HomeStat homeStat = new HomeStat();
        homeStat.setFarmCattleList(statCattle(groupByFarm));
        List<CattleDTO> currentFarmCattleList = cattleList.stream().filter(item -> item.getFarmCode().equals(farmCode)).toList();
        Map<String, List<CattleDTO>> groupByFarmZone = currentFarmCattleList.stream().collect(Collectors.groupingBy(CattleDTO::getFarmZoneCode));
        homeStat.setFarmZoneCattleList(statCattle(groupByFarmZone));
        return homeStat;
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
