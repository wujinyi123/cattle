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

    BreedRegisterDTO getBreedRegister(@Param("registerId") String registerId);

    int addBreedRegister(BreedRegisterDTO dto);

    int delBreedRegister(List<Integer> list);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheck(BreedPregnancyCheckQO qo);

    List<BreedPregnancyCheckDTO> listBreedPregnancyCheckByIds(List<Integer> list);

    int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int delBreedPregnancyCheck(List<Integer> list);

    List<BreedPregnancyResultDTO> listBreedPregnancyResult(BreedPregnancyResultQO qo);

    List<BreedPregnancyResultDTO> listBreedPregnancyResultByIds(List<Integer> list);

    int addBreedPregnancyResult(BreedPregnancyResultDTO dto);

    int delBreedPregnancyResult(List<Integer> list);
}
