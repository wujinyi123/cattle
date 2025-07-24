package com.manage.cattle.qo.base;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleTransferQO extends PageQO {
    private String reviewId;
    private String currentFarmCode;
    private String oldCattleCode;
    private String newCattleCode;
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
