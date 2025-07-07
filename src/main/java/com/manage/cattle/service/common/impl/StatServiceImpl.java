package com.manage.cattle.service.common.impl;

import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.common.HomeStat;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.service.common.StatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {
    @Resource
    private CattleDao cattleDao;

    @Override
    public HomeStat homeStat() {
        List<CattleDTO> cattleList = cattleDao.listCattle(new CattleQO());
        Map<String, List<CattleDTO>> groupByFarm = cattleList.stream().collect(Collectors.groupingBy(CattleDTO::getFarmName));
        List<HomeStat.Node> farmCattle = new ArrayList<>();
        for (Map.Entry<String, List<CattleDTO>> entry : groupByFarm.entrySet()) {
            HomeStat.Node node = new HomeStat.Node();
            node.setLabel(entry.getKey());
            node.setIntValue(entry.getValue().size());
            farmCattle.add(node);
        }
        farmCattle.sort((a,b)->b.getIntValue()-a.getIntValue());
        HomeStat homeStat = new HomeStat();
        homeStat.setFarmCattleList(farmCattle.size() > 10 ? farmCattle.subList(0, 10) : farmCattle);
        return homeStat;
    }
}
