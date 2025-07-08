package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.qo.base.FarmQO;
import com.manage.cattle.qo.base.FarmZoneQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FarmDao {
    List<FarmDTO> listFarm(FarmQO qo);

    FarmDTO getFarmById(@Param("farmId") String farmId);

    FarmDTO getFarm(@Param("farmName") String farmName);

    int addFarm(FarmDTO dto);

    int updateFarm(FarmDTO dto);

    int saveAdmin(FarmDTO dto);

    int delFarm(List<String> list);

    List<FarmZoneDTO> listFarmZone(FarmZoneQO qo);

    FarmZoneDTO getFarmZone(@Param("farmZoneCode") String farmZoneCode);

    int addFarmZone(FarmZoneDTO dto);

    int updateFarmZone(FarmZoneDTO dto);

    int delFarmZone(List<String> list);
}
