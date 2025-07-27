package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.CattleChangeZoneDTO;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.CattleTransferDTO;
import com.manage.cattle.qo.base.CattleChangeZoneQO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.CattleTransferQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CattleDao {
    List<CattleDTO> listCattle(CattleQO qo);

    CattleDTO getCattle(@Param("cattleCode") String cattleCode);

    int addCattle(CattleDTO dto);

    int updateCattle(CattleDTO dto);

    int delCattle(List<String> list);

    int updateCattleZone(CattleChangeZoneDTO dto);

    int transferCattle(CattleDTO dto);

    List<CattleChangeZoneDTO> listCattleChangeZone(CattleChangeZoneQO qo);

    int addCattleChangeZone(CattleChangeZoneDTO dto);

    List<CattleTransferDTO> listCattleTransfer(CattleTransferQO qo);

    int countCattleTransferWork(@Param("oldCattleCode") String oldCattleCode);

    CattleTransferDTO getCattleTransfer(@Param("reviewId") String reviewId);

    int addCattleTransfer(CattleTransferDTO dto);

    int updateCattleTransferApprover(CattleTransferDTO dto);

    int updateCattleTransferStatus(CattleTransferDTO dto);
}
