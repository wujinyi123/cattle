package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.qo.base.FarmQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FarmDao {
    List<FarmDTO> listFarm(FarmQO qo);

    FarmDTO getFarmById(@Param("farmId") String farmId);

    FarmDTO getFarm(@Param("farmName") String farmName);

    int addFarm(FarmDTO dto);

    int updateFarm(FarmDTO dto);

    int saveAdminEmployee(FarmDTO dto);

    int delFarm(List<String> list);
}
