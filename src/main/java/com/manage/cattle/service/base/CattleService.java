package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.CattleTransferDTO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.CattleTransferQO;

import java.util.List;
import java.util.Map;

public interface CattleService {
    PageInfo<CattleDTO> pageCattle(CattleQO qo);

    List<CattleDTO> listCattle(CattleQO qo);

    CattleDTO getCattle(String cattleCode);

    int saveCattle(String type, CattleDTO dto);

    int delCattle(List<String> cattleCodeList);

    List<String> importCattle(List<CattleDTO> importList);

    PageInfo<CattleTransferDTO> pageCattleTransfer(CattleTransferQO qo);

    int addCattleTransfer(CattleTransferDTO dto);

    int updateCattleTransferApprover(CattleTransferDTO dto);

    int updateCattleTransferStatus(CattleTransferDTO dto);

    Map<?,?> getCattleTransferNum(String currentFarmCode);
}
