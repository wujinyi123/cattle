package com.manage.cattle.dao.inventory;

import com.manage.cattle.dto.inventory.InventoryBuyDTO;
import com.manage.cattle.dto.inventory.InventoryDeathDTO;
import com.manage.cattle.dto.inventory.InventorySellDTO;
import com.manage.cattle.qo.inventory.InventoryBuyQO;
import com.manage.cattle.qo.inventory.InventoryDeathQO;
import com.manage.cattle.qo.inventory.InventorySellQO;

import java.util.List;

public interface InventoryDao {
    List<InventoryBuyDTO> listInventoryBuy(InventoryBuyQO qo);

    int addInventoryBuy(InventoryBuyDTO dto);

    int delInventoryBuy(List<Integer> ids);

    List<InventorySellDTO> listInventorySell(InventorySellQO qo);

    int addInventorySell(InventorySellDTO dto);

    int batchAddInventorySell(List<InventorySellDTO> list);

    int delInventorySell(List<Integer> ids);

    List<InventoryDeathDTO> listInventoryDeath(InventoryDeathQO qo);

    int addInventoryDeath(InventoryDeathDTO dto);

    int batchAddInventoryDeath(List<InventoryDeathDTO> list);

    int delInventoryDeath(List<Integer> ids);
}
