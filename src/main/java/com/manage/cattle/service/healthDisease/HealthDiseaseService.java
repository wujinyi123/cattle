package com.manage.cattle.service.healthDisease;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.healthDisease.ImmunityRegisterDTO;
import com.manage.cattle.dto.healthDisease.QuarantineRegisterDTO;
import com.manage.cattle.qo.healthDisease.ImmunityRegisterQO;
import com.manage.cattle.qo.healthDisease.QuarantineRegisterQO;

import java.util.List;

public interface HealthDiseaseService {
    PageInfo<QuarantineRegisterDTO> pageQuarantineRegister(QuarantineRegisterQO qo);

    int addQuarantineRegister(QuarantineRegisterDTO dto);

    int delQuarantineRegister(List<Integer> ids);

    PageInfo<ImmunityRegisterDTO> pageImmunityRegister(ImmunityRegisterQO qo);

    int addImmunityRegister(ImmunityRegisterDTO dto);

    int delImmunityRegister(List<Integer> ids);
}
