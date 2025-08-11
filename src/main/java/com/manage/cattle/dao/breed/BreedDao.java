package com.manage.cattle.dao.breed;

import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BreedDao {
    List<BreedRegisterDTO> listBreedRegister(BreedRegisterQO qo);

    List<BreedRegisterDTO> listBreedRegisterByIds(List<Integer> list);

    List<BreedRegisterDTO> listBreedingCattleCode(List<String> list);

    BreedRegisterDTO getBreedRegister(@Param("registerId") String registerId);

    int addBreedRegister(BreedRegisterDTO dto);

    int batchAddBreedRegister(BreedRegisterDTO dto);

    int delBreedRegister(List<Integer> list);

    int delBreedPregnancyCheckByRegisterId(List<String> list);

    int delBreedPregnancyResultByRegisterId(List<String> list);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheck(BreedPregnancyCheckQO qo);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheckByIds(List<Integer> list);

    int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int batchAddBreedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int delBreedPregnancyCheck(List<Integer> list);

    List<BreedPregnancyResultDTO> listBreedPregnancyResult(BreedPregnancyResultQO qo);

    List<BreedPregnancyResultDTO> listBreedPregnancyResultByIds(List<Integer> list);

    int addBreedPregnancyResult(BreedPregnancyResultDTO dto);

    int delBreedPregnancyResult(List<Integer> list);
}
