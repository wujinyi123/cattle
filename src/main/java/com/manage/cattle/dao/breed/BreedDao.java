package com.manage.cattle.dao.breed;

import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BreedDao {
    List<BreedRegisterDTO> listBeedRegister(BreedRegisterQO qo);

    List<BreedRegisterDTO> listBeedRegisterByIds(List<Integer> list);

    BreedRegisterDTO getBeedRegister(@Param("registerId") String registerId);

    int addBeedRegister(BreedRegisterDTO dto);

    int delBeedRegister(List<Integer> list);

    List<BreedPregnancyCheckDTO> listBeedPregnancyCheck(BreedPregnancyCheckQO qo);

    List<BreedPregnancyCheckDTO> listBeedPregnancyCheckByIds(List<Integer> list);

    int addBeedPregnancyCheck(BreedPregnancyCheckDTO dto);

    int delBeedPregnancyCheck(List<Integer> list);
}
