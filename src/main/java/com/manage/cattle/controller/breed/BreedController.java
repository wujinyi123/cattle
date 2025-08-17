package com.manage.cattle.controller.breed;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.breed.BreedFrozenSemenDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedFrozenSemenQO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import com.manage.cattle.service.bread.BreedService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/breed")
public class BreedController {
    @Resource
    private BreedService breedService;

    @GetMapping("/pageBreedFrozenSemen")
    public PageInfo<BreedFrozenSemenDTO> pageBreedFrozenSemen(BreedFrozenSemenQO qo) {
        return breedService.pageBreedFrozenSemen(qo);
    }

    @GetMapping("/listBreedFrozenSemen")
    public List<BreedFrozenSemenDTO> listBreedFrozenSemen(BreedFrozenSemenQO qo) {
        return breedService.listBreedFrozenSemen(qo);
    }

    @GetMapping("/getBreedFrozenSemen")
    public BreedFrozenSemenDTO getBreedFrozenSemen(@RequestParam String frozenSemenCode) {
        return breedService.getBreedFrozenSemen(frozenSemenCode);
    }

    @PostMapping("/saveBreedFrozenSemen")
    public int saveBreedFrozenSemen(@RequestParam String type, @RequestBody BreedFrozenSemenDTO dto) {
        return breedService.saveBreedFrozenSemen(type, dto);
    }

    @PostMapping("/delBreedFrozenSemen")
    public int delBreedFrozenSemen(@RequestBody List<Integer> ids) {
        return breedService.delBreedFrozenSemen(ids);
    }

    @GetMapping("/pageBreedRegister")
    public PageInfo<BreedRegisterDTO> pageBreedRegister(BreedRegisterQO qo) {
        return breedService.pageBreedRegister(qo);
    }

    @PostMapping("/addBreedRegister")
    public int addBreedRegister(@RequestBody BreedRegisterDTO dto) {
        return breedService.addBreedRegister(dto);
    }

    @PostMapping("/delBreedRegister")
    public int delBreedRegister(@RequestBody List<Integer> ids) {
        return breedService.delBreedRegister(ids);
    }

    @GetMapping("/pageBreedPregnancyCheck")
    public PageInfo<BreedPregnancyCheckDTO> pageBreedPregnancyCheck(BreedPregnancyCheckQO qo) {
        return breedService.pageBreedPregnancyCheck(qo);
    }

    @PostMapping("/addBreedPregnancyCheck")
    public int addBreedPregnancyCheck(@RequestBody BreedPregnancyCheckDTO dto) {
        return breedService.addBreedPregnancyCheck(dto);
    }

    @PostMapping("/delBreedPregnancyCheck")
    public int delBreedPregnancyCheck(@RequestBody List<Integer> ids) {
        return breedService.delBreedPregnancyCheck(ids);
    }

    @GetMapping("/pageBreedPregnancyResult")
    public PageInfo<BreedPregnancyResultDTO> pageBreedPregnancyResult(BreedPregnancyResultQO qo) {
        return breedService.pageBreedPregnancyResult(qo);
    }

    @PostMapping("/addBreedPregnancyResult")
    public int addBreedPregnancyResult(@RequestBody BreedPregnancyResultDTO dto) {
        return breedService.addBreedPregnancyResult(dto);
    }

    @PostMapping("/delBreedPregnancyResult")
    public int delBreedPregnancyResult(@RequestBody List<Integer> ids) {
        return breedService.delBreedPregnancyResult(ids);
    }
}
