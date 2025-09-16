package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.CattleChangeZoneDTO;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.CattleTransferDTO;
import com.manage.cattle.dto.base.CattleTransferReviewDTO;
import com.manage.cattle.qo.base.CattleChangeZoneQO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.CattleTransferQO;
import com.manage.cattle.qo.base.CattleTransferReviewQO;
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

    int batchTransferCattle(@Param("updateUser") String updateUser,
                            @Param("cattleCodeList") String cattleCodeList,
                            @Param("farmZoneCode") String farmZoneCode);

    List<CattleChangeZoneDTO> listCattleChangeZone(CattleChangeZoneQO qo);

    int addCattleChangeZone(CattleChangeZoneDTO dto);

    List<CattleTransferDTO> listCattleTransfer(CattleTransferQO qo);

    int countCattleTransferWork(@Param("oldCattleCode") String oldCattleCode);

    CattleTransferDTO getCattleTransfer(@Param("reviewId") String reviewId);

    int addCattleTransfer(CattleTransferDTO dto);

    int updateCattleTransferApprover(CattleTransferDTO dto);

    int updateCattleTransferStatus(CattleTransferDTO dto);


    List<CattleTransferReviewDTO> listCattleTransferReview(CattleTransferReviewQO qo);

    List<String> listCattleTransferReviewWork();

    CattleTransferReviewDTO getCattleTransferReview(@Param("reviewId") String reviewId);

    int addCattleTransferReview(CattleTransferReviewDTO dto);

    int updateCattleTransferReviewApprover(CattleTransferReviewDTO dto);

    int updateCattleTransferReviewStatus(CattleTransferReviewDTO dto);
}
