package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.CattleChangeZoneDTO;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.CattleTransferDTO;
import com.manage.cattle.dto.base.CattleTransferReviewDTO;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.qo.base.CattleChangeZoneQO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.CattleTransferQO;
import com.manage.cattle.qo.base.CattleTransferReviewQO;

import java.util.List;
import java.util.Map;

public interface CattleService {
    PageInfo<CattleDTO> pageCattle(CattleQO qo);

    List<CattleDTO> listCattle(CattleQO qo);

    FileByteInfo exportCatailDetail(CattleQO qo);

    CattleDTO getCattle(String cattleCode);

    int saveCattle(String type, CattleDTO dto);

    int delCattle(List<String> cattleCodeList);

    Map<Integer, String> importCattle(List<CattleDTO> importList);

    PageInfo<CattleChangeZoneDTO> pageCattleChangeZone(CattleChangeZoneQO qo);

    int addCattleChangeZone(CattleChangeZoneDTO dto);

    PageInfo<CattleTransferDTO> pageCattleTransfer(CattleTransferQO qo);

    int addCattleTransfer(CattleTransferDTO dto);

    int updateCattleTransferApprover(CattleTransferDTO dto);

    int updateCattleTransferStatus(CattleTransferDTO dto);

    Map<?,?> getCattleTransferNum(String currentFarmCode);

    PageInfo<CattleTransferReviewDTO> pageCattleTransferReview(CattleTransferReviewQO qo);

    int addCattleTransferReview(CattleTransferReviewDTO dto);

    int updateCattleTransferReviewApprover(CattleTransferReviewDTO dto);

    int updateCattleTransferReviewStatus(CattleTransferReviewDTO dto);

    Map<?,?> getCattleTransferReviewNum(String currentFarmCode);
}
