package com.manage.cattle.qo.common;

import com.manage.cattle.qo.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysConfigQO extends PageQO {
    private String code;
    private List<String> codeList;
}
