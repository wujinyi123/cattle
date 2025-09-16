package com.manage.cattle.service.inventory.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.inventory.InventoryDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.inventory.InventoryBuyDTO;
import com.manage.cattle.dto.inventory.InventoryDeathDTO;
import com.manage.cattle.dto.inventory.InventorySellDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.inventory.InventoryBuyQO;
import com.manage.cattle.qo.inventory.InventoryDeathQO;
import com.manage.cattle.qo.inventory.InventorySellQO;
import com.manage.cattle.service.inventory.inventoryService;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class inventoryServiceImpl implements inventoryService {
    @Resource
    private InventoryDao inventoryDao;

    @Resource
    private FarmDao farmDao;

    @Resource
    private CattleDao cattleDao;

    @Override
    public PageInfo<InventoryBuyDTO> pageInventoryBuy(InventoryBuyQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(inventoryDao.listInventoryBuy(qo));
    }

    @Override
    public List<InventoryBuyDTO> listInventoryBuy(InventoryBuyQO qo) {
        return inventoryDao.listInventoryBuy(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addInventoryBuy(InventoryBuyDTO dto) {
        if (cattleDao.getCattle(dto.getCattleCode()) != null) {
            throw new BusinessException("耳牌号已存在");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
        if (farmZoneDTO == null) {
            throw new BusinessException("圈舍编号不存在");
        }
        CattleQO cattleQO = new CattleQO();
        cattleQO.setFarmZoneCode(dto.getFarmZoneCode());
        List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
        if (farmZoneDTO.getSize() <= cattleList.size()) {
            throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
        }
        dto.setSource("购入");
        if (cattleDao.addCattle(dto) == 0) {
            throw new BusinessException("添加牛只失败");
        }
        int result = inventoryDao.addInventoryBuy(dto);
        if (result == 0) {
            throw new BusinessException("添加失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delInventoryBuy(List<Integer> ids) {
        return inventoryDao.delInventoryBuy(ids);
    }

    @Override
    public PageInfo<InventorySellDTO> pageInventorySell(InventorySellQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(inventoryDao.listInventorySell(qo));
    }

    @Override
    public List<InventorySellDTO> listInventorySell(InventorySellQO qo) {
        return inventoryDao.listInventorySell(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addInventorySell(InventorySellDTO dto) {
        CattleQO qo = new CattleQO();
        qo.setFarmCode(dto.getFarmCode());
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> codeList = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        List<String> errorCodeList = dto.getCattleCodeList().stream().filter(item -> !codeList.contains(item)).toList();
        if (!errorCodeList.isEmpty()) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCodeList) + ")不正确或非当前牛场的");
        }
        String username = UserUtil.getCurrentUsername();
        List<InventorySellDTO> sellList = new ArrayList<>();
        for (CattleDTO cattleDTO : cattleList) {
            InventorySellDTO sellDTO = new InventorySellDTO();
            sellDTO.setCreateUser(username);
            sellDTO.setUpdateUser(username);
            sellDTO.setFarmCode(cattleDTO.getFarmCode());
            sellDTO.setCattleCode(cattleDTO.getCattleCode());
            sellDTO.setCattleInfo(JSONUtil.toJsonStr(cattleDTO));
            sellDTO.setBuyerInfo(dto.getBuyerInfo());
            sellDTO.setPrice(dto.getPrice());
            sellDTO.setQuarantineCertificate(dto.getQuarantineCertificate());
            sellDTO.setSellDay(dto.getSellDay());
            sellList.add(sellDTO);
        }
        if (cattleDao.delCattle(codeList) == 0) {
            throw new BusinessException("删除牛只失败");
        }
        int result = inventoryDao.batchAddInventorySell(sellList);
        if (result == 0) {
            throw new BusinessException("添加失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delInventorySell(List<Integer> ids) {
        return inventoryDao.delInventorySell(ids);
    }

    @Override
    public PageInfo<InventoryDeathDTO> pageInventoryDeath(InventoryDeathQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(inventoryDao.listInventoryDeath(qo));
    }

    @Override
    public List<InventoryDeathDTO> listInventoryDeath(InventoryDeathQO qo) {
        return inventoryDao.listInventoryDeath(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addInventoryDeath(InventoryDeathDTO dto) {
        CattleQO qo = new CattleQO();
        qo.setFarmCode(dto.getFarmCode());
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> codeList = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        List<String> errorCodeList = dto.getCattleCodeList().stream().filter(item -> !codeList.contains(item)).toList();
        if (!errorCodeList.isEmpty()) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCodeList) + ")不正确或非当前牛场的");
        }
        String username = UserUtil.getCurrentUsername();
        List<InventoryDeathDTO> deathList = new ArrayList<>();
        for (CattleDTO cattleDTO : cattleList) {
            InventoryDeathDTO deathDTO = new InventoryDeathDTO();
            deathDTO.setCreateUser(username);
            deathDTO.setUpdateUser(username);
            deathDTO.setFarmCode(cattleDTO.getFarmCode());
            deathDTO.setCattleCode(cattleDTO.getCattleCode());
            deathDTO.setCattleInfo(JSONUtil.toJsonStr(cattleDTO));
            deathDTO.setReason(dto.getReason());
            deathDTO.setHandleMethod(dto.getHandleMethod());
            deathDTO.setDeathDay(dto.getDeathDay());
            deathList.add(deathDTO);
        }
        if (cattleDao.delCattle(codeList) == 0) {
            throw new BusinessException("删除牛只失败");
        }
        int result = inventoryDao.batchAddInventoryDeath(deathList);
        if (result == 0) {
            throw new BusinessException("添加失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delInventoryDeath(List<Integer> ids) {
        return inventoryDao.delInventoryDeath(ids);
    }

}
