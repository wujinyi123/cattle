package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.FarmZoneQO;

import java.util.List;

public interface FarmService {
    PageInfo<FarmDTO> pageFarm(FarmQO qo);

    List<FarmDTO> listFarm(FarmQO qo);

    FarmDTO getFarm(String farmCode);

    int saveFarm(String type, FarmDTO dto);

    int delFarm(List<String> farmCodeList);

    PageInfo<FarmZoneDTO> pageFarmZone(FarmZoneQO qo);

    List<FarmZoneDTO> listFarmZone(FarmZoneQO qo);

    FarmZoneDTO getFarmZone(String farmZoneCode);

    int saveFarmZone(String type, FarmZoneDTO dto);

    int delFarmZone(List<String> farmZoneCodeList);
}
