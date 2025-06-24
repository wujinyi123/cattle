package com.manage.cattle.dao.base;

import com.manage.cattle.dto.NodeDTO;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.qo.base.CattleQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CattleDao {
    List<CattleDTO> listCattle(CattleQO qo);

    CattleDTO getCattleById(@Param("cattleId") String cattleId);

    CattleDTO getCattle(@Param("cattleCode") String cattleCode);

    int addCattle(CattleDTO dto);

    int updateCattle(CattleDTO dto);

    int delCattle(List<String> list);

    List<NodeDTO> treeCattle();
}
