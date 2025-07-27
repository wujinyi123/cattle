package com.manage.cattle.dao.healthDisease;

import com.manage.cattle.dto.healthDisease.ImmunityRegisterDTO;
import com.manage.cattle.dto.healthDisease.QuarantineRegisterDTO;
import com.manage.cattle.qo.healthDisease.ImmunityRegisterQO;
import com.manage.cattle.qo.healthDisease.QuarantineRegisterQO;

import java.util.List;

public interface HealthDiseaseDao {
    List<QuarantineRegisterDTO> listQuarantineRegister(QuarantineRegisterQO qo);

    int addQuarantineRegister(QuarantineRegisterDTO dto);

    int delQuarantineRegister(List<Integer> list);

    List<ImmunityRegisterDTO> listImmunityRegister(ImmunityRegisterQO qo);

    int addImmunityRegister(ImmunityRegisterDTO dto);

    int delImmunityRegister(List<Integer> list);
}
