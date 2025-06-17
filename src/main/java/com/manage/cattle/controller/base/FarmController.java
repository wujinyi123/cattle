package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.qo.base.FarmQO;
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

    @GetMapping("/getFarm")
    public FarmDTO getFarm(@RequestParam("farmId") String farmId) {
        return farmService.getFarm(farmId);
    }

    @PostMapping("/saveFarm")
    public int saveFarm(@RequestParam("type") String type, @RequestBody FarmDTO dto) {
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
}
