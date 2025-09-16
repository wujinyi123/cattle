package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleTransferReviewDTO extends BaseDTO {
    private String reviewId;
    private String cattleCodeList;
    private String oldFarmCode;
    private String oldFarmName;
    private String newFarmCode;
    private String newFarmName;
    private String newFarmZoneCode;
    private String submitUser;
    private String submitTime;
    private String reason;
    private String approver;
    private String operator;
    private String operateTime;
    private String opinion;
    private String status;
}
