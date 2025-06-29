package com.manage.cattle.service.bread;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;

import java.util.List;

public interface BreedService {
    PageInfo<BreedRegisterDTO> pageBeedRegister(BreedRegisterQO qo);

    int addBeedRegister(BreedRegisterDTO dto);

    int delBeedRegister(List<Integer> ids);

    PageInfo<BreedPregnancyCheckDTO> pageBeedPregnancyCheck(BreedPregnancyCheckQO qo);

    int addBeedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int delBeedPregnancyCheck(List<Integer> ids);
}
