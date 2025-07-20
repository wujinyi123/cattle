package com.manage.cattle.controller.common;

import com.manage.cattle.dto.common.HomeStat;
import com.manage.cattle.service.common.StatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stat")
public class StatController {
    @Resource
    private StatService statService;

    @GetMapping("/homeStat")
    public HomeStat homeStat(@RequestParam String farmCode) {
        return statService.homeStat(farmCode);
    }
}
