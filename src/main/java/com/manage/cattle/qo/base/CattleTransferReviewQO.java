package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleTransferReviewQO extends PageQO {
    private String reviewId;
    private String currentFarmCode;
    private String cattleCode;
    private String oldFarmCode;
    private String newFarmCode;
    private String submitUser;
    private String submitTimeStart;
    private String submitTimeEnd;
    private String reason;
    private String approver;
    private String operator;
    private String operateTimeStart;
    private String operateTimeEnd;
    private String opinion;
    private String status;
}
