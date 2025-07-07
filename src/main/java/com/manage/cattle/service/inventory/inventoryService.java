package com.manage.cattle.service.inventory;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.inventory.InventoryBuyDTO;
import com.manage.cattle.dto.inventory.InventoryDeathDTO;
import com.manage.cattle.dto.inventory.InventorySellDTO;
import com.manage.cattle.qo.inventory.InventoryBuyQO;
import com.manage.cattle.qo.inventory.InventoryDeathQO;
import com.manage.cattle.qo.inventory.InventorySellQO;

import java.util.List;

public interface inventoryService {
    PageInfo<InventoryBuyDTO> pageInventoryBuy(InventoryBuyQO qo);

    List<InventoryBuyDTO> listInventoryBuy(InventoryBuyQO qo);

    int addInventoryBuy(InventoryBuyDTO dto);

    int delInventoryBuy(List<Integer> ids);

    PageInfo<InventorySellDTO> pageInventorySell(InventorySellQO qo);

    List<InventorySellDTO> listInventorySell(InventorySellQO qo);

    int addInventorySell(InventorySellDTO dto);

    int delInventorySell(List<Integer> ids);

    PageInfo<InventoryDeathDTO> pageInventoryDeath(InventoryDeathQO qo);

    List<InventoryDeathDTO> listInventoryDeath(InventoryDeathQO qo);

    int addInventoryDeath(InventoryDeathDTO dto);

    int delInventoryDeath(List<Integer> ids);
}
