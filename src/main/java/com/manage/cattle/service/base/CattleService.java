package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.qo.base.CattleQO;

import java.util.List;

public interface CattleService {
    PageInfo<CattleDTO> pageCattle(CattleQO qo);

    List<CattleDTO> listCattle(CattleQO qo);

    List<String> importCattle(String requireFields, List<CattleDTO> list);

    CattleDTO getCattle(String cattleCode);

    int saveCattle(String type, CattleDTO dto);

    int delCattle(List<String> cattleCodeList);
}
