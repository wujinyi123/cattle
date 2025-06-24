package com.manage.cattle.dao.breed;

import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.qo.breed.BreedRegisterQO;

import java.util.List;

public interface BreedDao {
    List<BreedRegisterDTO> listBeedRegister(BreedRegisterQO qo);

    List<BreedRegisterDTO> listBeedRegisterByIds(List<Integer> list);

    int addBeedRegister(BreedRegisterDTO dto);

    int delBeedRegister(List<Integer> list);
}
