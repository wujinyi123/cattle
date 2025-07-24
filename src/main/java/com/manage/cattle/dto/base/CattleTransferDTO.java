package com.manage.cattle.dto.base;

import com.manage.cattle.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CattleTransferDTO extends BaseDTO {
    private String reviewId;
    private String oldFarmCode;
    private String oldFarmName;
    private String oldCattleCode;
    private String oldCattleInfo;
    private String newFarmCode;
    private String newFarmName;
    private String newFarmZoneCode;
    private String newCattleCode;
    private String newCattleInfo;
    private String submitUser;
    private String submitTime;
    private String reason;
    private String approver;
    private String operator;
    private String operateTime;
    private String opinion;
    private String status;
}
