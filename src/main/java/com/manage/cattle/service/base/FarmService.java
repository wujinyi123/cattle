package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.qo.base.FarmQO;

import java.util.List;

public interface FarmService {
    PageInfo<FarmDTO> pageFarm(FarmQO qo);

    int saveFarm(String type, FarmDTO dto);

    int delFarm(List<String> farmIds);
}
