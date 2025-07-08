package com.manage.cattle.service.inventory.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.inventory.InventoryDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.inventory.InventoryBuyDTO;
import com.manage.cattle.dto.inventory.InventoryDeathDTO;
import com.manage.cattle.dto.inventory.InventorySellDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.inventory.InventoryBuyQO;
import com.manage.cattle.qo.inventory.InventoryDeathQO;
import com.manage.cattle.qo.inventory.InventorySellQO;
import com.manage.cattle.service.inventory.inventoryService;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        FarmDTO farmDTO = farmDao.getFarmById(dto.getFarmId());
        if (farmDTO == null) {
            throw new BusinessException("牧场不存在");
        }
        if (!"Y".equals(isSysAdmin) && !username.equals(farmDTO.getOwner())) {
            throw new BusinessException("权限不足");
        }
        FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
        if (farmZoneDTO == null) {
            throw new BusinessException("圈舍编号不存在");
        }
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (cattleDTO != null) {
            throw new BusinessException("耳牌号已存在");
        }
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
        InventoryBuyQO qo = new InventoryBuyQO();
        qo.setIds(ids);
        List<InventoryBuyDTO> list = inventoryDao.listInventoryBuy(qo);
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin)) {
            return inventoryDao.delInventoryBuy(ids);
        }
        String username = UserUtil.getUsername();
        if (list.stream().anyMatch(item -> !username.equals(item.getFarmOwner()))) {
            throw new BusinessException("部分权限不足");
        }
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
        String username = UserUtil.getUsername();
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (cattleDTO == null) {
            throw new BusinessException("耳牌号不存在");
        }
        dto.setFarmId(cattleDTO.getFarmId());
        dto.setCattleInfo(JSONUtil.toJsonStr(cattleDTO));
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !username.equals(cattleDTO.getFarmOwner())) {
            throw new BusinessException("权限不足");
        }
        if (cattleDao.delCattle(List.of(dto.getCattleCode())) == 0) {
            throw new BusinessException("删除牛只失败");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        int result = inventoryDao.addInventorySell(dto);
        if (result == 0) {
            throw new BusinessException("添加失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delInventorySell(List<Integer> ids) {
        InventorySellQO qo = new InventorySellQO();
        qo.setIds(ids);
        List<InventorySellDTO> list = inventoryDao.listInventorySell(qo);
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin)) {
            return inventoryDao.delInventorySell(ids);
        }
        String username = UserUtil.getUsername();
        if (list.stream().anyMatch(item -> !username.equals(item.getFarmOwner()))) {
            throw new BusinessException("部分权限不足");
        }
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
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (cattleDTO == null) {
            throw new BusinessException("耳牌号不存在");
        }
        dto.setFarmId(cattleDTO.getFarmId());
        dto.setCattleInfo(JSONUtil.toJsonStr(cattleDTO));
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !username.equals(cattleDTO.getFarmOwner())) {
            throw new BusinessException("权限不足");
        }
        if (cattleDao.delCattle(List.of(dto.getCattleCode())) == 0) {
            throw new BusinessException("删除牛只失败");
        }
        int result = inventoryDao.addInventoryDeath(dto);
        if (result == 0) {
            throw new BusinessException("添加失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delInventoryDeath(List<Integer> ids) {
        InventoryDeathQO qo = new InventoryDeathQO();
        qo.setIds(ids);
        List<InventoryDeathDTO> list = inventoryDao.listInventoryDeath(qo);
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin)) {
            return inventoryDao.delInventoryDeath(ids);
        }
        String username = UserUtil.getUsername();
        if (list.stream().anyMatch(item -> !username.equals(item.getFarmOwner()))) {
            throw new BusinessException("部分权限不足");
        }
        return inventoryDao.delInventoryDeath(ids);
    }

}
