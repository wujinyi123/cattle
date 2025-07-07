package com.manage.cattle.controller.inventory;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.inventory.InventoryBuyDTO;
import com.manage.cattle.dto.inventory.InventoryDeathDTO;
import com.manage.cattle.dto.inventory.InventorySellDTO;
import com.manage.cattle.qo.inventory.InventoryBuyQO;
import com.manage.cattle.qo.inventory.InventoryDeathQO;
import com.manage.cattle.qo.inventory.InventorySellQO;
import com.manage.cattle.service.inventory.inventoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Resource
    private inventoryService inventoryService;

    @GetMapping("/pageInventoryBuy")
    public PageInfo<InventoryBuyDTO> pageInventoryBuy(InventoryBuyQO qo) {
        return inventoryService.pageInventoryBuy(qo);
    }

    @PostMapping("/addInventoryBuy")
    public int addInventoryBuy(@RequestBody InventoryBuyDTO dto) {
        return inventoryService.addInventoryBuy(dto);
    }

    @PostMapping("/delInventoryBuy")
    public int delInventoryBuy(@RequestBody List<Integer> ids) {
        return inventoryService.delInventoryBuy(ids);
    }

    @GetMapping("/pageInventorySell")
    public PageInfo<InventorySellDTO> pageInventorySell(InventorySellQO qo) {
        return inventoryService.pageInventorySell(qo);
    }

    @PostMapping("/addInventorySell")
    public int addInventorySell(@RequestBody InventorySellDTO dto) {
        return inventoryService.addInventorySell(dto);
    }

    @PostMapping("/delInventorySell")
    public int delInventorySell(@RequestBody List<Integer> ids) {
        return inventoryService.delInventorySell(ids);
    }

    @GetMapping("/pageInventoryDeath")
    public PageInfo<InventoryDeathDTO> pageInventoryDeath(InventoryDeathQO qo) {
        return inventoryService.pageInventoryDeath(qo);
    }

    @PostMapping("/addInventoryDeath")
    public int addInventoryDeath(@RequestBody InventoryDeathDTO dto) {
        return inventoryService.addInventoryDeath(dto);
    }

    @PostMapping("/delInventoryDeath")
    public int delInventoryDeath(@RequestBody List<Integer> ids) {
        return inventoryService.delInventoryDeath(ids);
    }
}
