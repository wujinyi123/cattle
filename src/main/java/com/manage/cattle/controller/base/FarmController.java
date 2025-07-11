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
    public List<FarmDTO> listFarm(FarmQO qo) {
        return farmService.listFarm(qo);
    }

    @GetMapping("/getFarm")
    public FarmDTO getFarm(@RequestParam String farmCode) {
        return farmService.getFarm(farmCode);
    }

    @PostMapping("/saveFarm")
    public int saveFarm(@RequestParam String type, @RequestBody FarmDTO dto) {
        return farmService.saveFarm(type, dto);
    }

    @PostMapping("/delFarm")
    public int delFarm(@RequestBody List<String> farmCodeList) {
        return farmService.delFarm(farmCodeList);
    }

    @GetMapping("/pageFarmZone")
    public PageInfo<FarmZoneDTO> pageFarmZone(FarmZoneQO qo) {
        return farmService.pageFarmZone(qo);
    }

    @GetMapping("/listFarmZone")
    public List<FarmZoneDTO> listFarmZone(FarmZoneQO qo) {
        return farmService.listFarmZone(qo);
    }

    @GetMapping("/getFarmZone")
    public FarmZoneDTO getFarmZone(@RequestParam String farmZoneCode) {
        return farmService.getFarmZone(farmZoneCode);
    }

    @PostMapping("/saveFarmZone")
    public int saveFarmZone(@RequestParam String type, @RequestBody FarmZoneDTO dto) {
        return farmService.saveFarmZone(type, dto);
    }

    @PostMapping("/delFarmZone")
    public int delFarmZone(@RequestBody List<String> farmZoneCodeList) {
        return farmService.delFarmZone(farmZoneCodeList);
    }
}
