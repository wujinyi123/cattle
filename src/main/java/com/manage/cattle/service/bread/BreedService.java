package com.manage.cattle.service.bread;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.breed.BreedFrozenSemenDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedFrozenSemenQO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;

import java.util.List;
import java.util.Map;

public interface BreedService {
    PageInfo<BreedFrozenSemenDTO> pageBreedFrozenSemen(BreedFrozenSemenQO qo);

    List<BreedFrozenSemenDTO> listBreedFrozenSemen(BreedFrozenSemenQO qo);

    Map<Integer, String> importBreedFrozenSemen(List<BreedFrozenSemenDTO> importList);

    BreedFrozenSemenDTO getBreedFrozenSemen(String frozenSemenCode);

    int saveBreedFrozenSemen(String type, BreedFrozenSemenDTO dto);

    int delBreedFrozenSemen(List<Integer> ids);

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
