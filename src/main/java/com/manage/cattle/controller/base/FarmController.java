package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.FarmZoneQO;
import com.manage.cattle.service.base.FarmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/farm")
public class FarmController {
    @Resource
    private FarmService farmService;

    @GetMapping("/pageFarm")
    public PageInfo<FarmDTO> pageFarm(FarmQO qo) {
        return farmService.pageFarm(qo);
    }

    @GetMapping("/listFarm")
    public List<FarmDTO> listFarm() {
        return farmService.listFarm();
    }

    @GetMapping("/getFarm")
    public FarmDTO getFarm(@RequestParam String farmId) {
        return farmService.getFarm(farmId);
    }

    @PostMapping("/saveFarm")
    public int saveFarm(@RequestParam String type, @RequestBody FarmDTO dto) {
        return farmService.saveFarm(type, dto);
    }

    @PostMapping("/saveAdminEmployee")
    public int saveAdminEmployee(@RequestBody FarmDTO dto) {
        return farmService.saveAdminEmployee(dto);
    }

    @PostMapping("/delFarm")
    public int delFarm(@RequestBody List<String> farmIds) {
        return farmService.delFarm(farmIds);
    }

    @GetMapping("/pageFarmZone")
    public PageInfo<FarmZoneDTO> pageFarmZone(FarmZoneQO qo) {
        return farmService.pageFarmZone(qo);
    }

    @GetMapping("/listFarmZone")
    public List<FarmZoneDTO> listFarmZone(@RequestParam String farmId) {
        return farmService.listFarmZone(farmId);
    }

    @GetMapping("/getFarmZone")
    public FarmZoneDTO getFarmZone(@RequestParam String farmZoneId) {
        return farmService.getFarmZone(farmZoneId);
    }

    @PostMapping("/saveFarmZone")
    public int saveFarmZone(@RequestParam String type, @RequestBody FarmZoneDTO dto) {
        return farmService.saveFarmZone(type, dto);
    }

    @PostMapping("/delFarmZone")
    public int delFarmZone(@RequestBody List<String> farmZoneIds) {
        return farmService.delFarmZone(farmZoneIds);
    }
}
