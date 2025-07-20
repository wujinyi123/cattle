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
        if (cattleDao.getCattle(dto.getCattleCode()) != null) {
            throw new BusinessException("耳牌号已存在");
        }
        String username = UserUtil.getPayloadVal("username");
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
        if (farmZoneDTO == null) {
            throw new BusinessException("圈舍编号不存在");
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
        String username = UserUtil.getPayloadVal("username");
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (cattleDTO == null) {
            throw new BusinessException("耳牌号不存在");
        }
        if (!StrUtil.equals(dto.getFarmCode(), cattleDTO.getFarmCode())) {
            throw new BusinessException("请输入当前牛场的牛只耳牌号");
        }
        dto.setCattleInfo(JSONUtil.toJsonStr(cattleDTO));
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
        String username = UserUtil.getPayloadVal("username");
        CattleDTO cattleDTO = cattleDao.getCattle(dto.getCattleCode());
        if (cattleDTO == null) {
            throw new BusinessException("耳牌号不存在");
        }
        if (!StrUtil.equals(dto.getFarmCode(), cattleDTO.getFarmCode())) {
            throw new BusinessException("请输入当前牛场的牛只耳牌号");
        }
        dto.setCattleInfo(JSONUtil.toJsonStr(cattleDTO));
        if (cattleDao.delCattle(List.of(dto.getCattleCode())) == 0) {
            throw new BusinessException("删除牛只失败");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        int result = inventoryDao.addInventoryDeath(dto);
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
