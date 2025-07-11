package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.qo.base.CattleQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CattleDao {
    List<CattleDTO> listCattle(CattleQO qo);

    CattleDTO getCattle(@Param("cattleCode") String cattleCode);

    int addCattle(CattleDTO dto);

    int updateCattle(CattleDTO dto);

    int delCattle(List<String> list);
}
