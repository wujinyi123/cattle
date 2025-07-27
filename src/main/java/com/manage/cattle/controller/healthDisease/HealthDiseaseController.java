package com.manage.cattle.controller.healthDisease;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.healthDisease.ImmunityRegisterDTO;
import com.manage.cattle.dto.healthDisease.QuarantineRegisterDTO;
import com.manage.cattle.qo.healthDisease.ImmunityRegisterQO;
import com.manage.cattle.qo.healthDisease.QuarantineRegisterQO;
import com.manage.cattle.service.healthDisease.HealthDiseaseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/healthDisease")
public class HealthDiseaseController {
    @Resource
    private HealthDiseaseService healthDiseaseService;

    @GetMapping("/pageQuarantineRegister")
    public PageInfo<QuarantineRegisterDTO> pageQuarantineRegister(QuarantineRegisterQO qo) {
        return healthDiseaseService.pageQuarantineRegister(qo);
    }

    @PostMapping("/addQuarantineRegister")
    public int addQuarantineRegister(@RequestBody QuarantineRegisterDTO dto) {
        return healthDiseaseService.addQuarantineRegister(dto);
    }

    @PostMapping("/delQuarantineRegister")
    public int delQuarantineRegister(@RequestBody List<Integer> ids) {
        return healthDiseaseService.delQuarantineRegister(ids);
    }

    @GetMapping("/pageImmunityRegister")
    public PageInfo<ImmunityRegisterDTO> pageImmunityRegister(ImmunityRegisterQO qo) {
        return healthDiseaseService.pageImmunityRegister(qo);
    }

    @PostMapping("/addImmunityRegister")
    public int addImmunityRegister(@RequestBody ImmunityRegisterDTO dto) {
        return healthDiseaseService.addImmunityRegister(dto);
    }

    @PostMapping("/delImmunityRegister")
    public int delImmunityRegister(@RequestBody List<Integer> ids) {
        return healthDiseaseService.delImmunityRegister(ids);
    }
}
