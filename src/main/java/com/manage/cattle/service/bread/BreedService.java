package com.manage.cattle.service.bread;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;

import java.util.List;

public interface BreedService {
    PageInfo<BreedRegisterDTO> pageBreedRegister(BreedRegisterQO qo);

    List<BreedRegisterDTO> listBreedRegister(BreedRegisterQO qo);

    int addBreedRegister(BreedRegisterDTO dto);

    int delBreedRegister(List<Integer> ids);

    PageInfo<BreedPregnancyCheckDTO> pageBreedPregnancyCheck(BreedPregnancyCheckQO qo);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheck(BreedPregnancyCheckQO qo);

    int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int delBreedPregnancyCheck(List<Integer> ids);

    PageInfo<BreedPregnancyResultDTO> pageBreedPregnancyResult(BreedPregnancyResultQO qo);

    List<BreedPregnancyResultDTO> listBreedPregnancyResult(BreedPregnancyResultQO qo);

    int addBreedPregnancyResult(BreedPregnancyResultDTO dto);

    int delBreedPregnancyResult(List<Integer> ids);

    List<BreedRegisterDTO> downloadRegisterCattle(BreedRegisterQO qo);
}
