package com.manage.cattle.dao.breed;

import com.manage.cattle.dto.breed.BreedFrozenSemenDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedFrozenSemenQO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;

import java.util.List;

public interface BreedDao {
    List<BreedFrozenSemenDTO> listBreedFrozenSemen(BreedFrozenSemenQO qo);

    BreedFrozenSemenDTO getBreedFrozenSemen(String frozenSemenCode);

    int addBreedFrozenSemen(BreedFrozenSemenDTO dto);

    int updateBreedFrozenSemen(BreedFrozenSemenDTO dto);

    int delBreedFrozenSemen(List<Integer> list);

    List<String> listRefFrozenSemenCode(List<Integer> list);

    List<BreedRegisterDTO> listBreedRegister(BreedRegisterQO qo);

    List<BreedRegisterDTO> listLastBreedRegister(List<String> list);

    int addBreedRegister(BreedRegisterDTO dto);

    int delBreedRegister(List<Integer> list);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheck(BreedPregnancyCheckQO qo);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheckByCattleCode(List<String> list);

    int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int delBreedPregnancyCheck(List<Integer> list);

    List<BreedPregnancyResultDTO> listBreedPregnancyResult(BreedPregnancyResultQO qo);

    List<BreedPregnancyResultDTO> listBreedPregnancyResultByCattleCode(List<String> list);

    int addBreedPregnancyResult(BreedPregnancyResultDTO dto);

    int delBreedPregnancyResult(List<Integer> list);
}
