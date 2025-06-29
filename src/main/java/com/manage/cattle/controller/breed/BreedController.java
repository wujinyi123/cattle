package com.manage.cattle.controller.breed;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import com.manage.cattle.service.bread.BreedService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/breed")
public class BreedController {
    @Resource
    private BreedService breedService;

    @GetMapping("/pageBeedRegister")
    public PageInfo<BreedRegisterDTO> pageBeedRegister(BreedRegisterQO qo) {
        return breedService.pageBeedRegister(qo);
    }

    @PostMapping("/addBeedRegister")
    public int addBeedRegister(@RequestBody BreedRegisterDTO dto) {
        return breedService.addBeedRegister(dto);
    }

    @PostMapping("/delBeedRegister")
    public int delBeedRegister(@RequestBody List<Integer> ids) {
        return breedService.delBeedRegister(ids);
    }

    @GetMapping("/pageBeedPregnancyCheck")
    public PageInfo<BreedPregnancyCheckDTO> pageBeedPregnancyCheck(BreedPregnancyCheckQO qo) {
        return breedService.pageBeedPregnancyCheck(qo);
    }

    @PostMapping("/addBeedPregnancyCheck")
    public int addBeedPregnancyCheck(@RequestBody BreedPregnancyCheckDTO dto) {
        return breedService.addBeedPregnancyCheck(dto);
    }

    @PostMapping("/delBeedPregnancyCheck")
    public int delBeedPregnancyCheck(@RequestBody List<Integer> ids) {
        return breedService.delBeedPregnancyCheck(ids);
    }
}
