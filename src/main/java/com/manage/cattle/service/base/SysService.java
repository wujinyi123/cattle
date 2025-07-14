package com.manage.cattle.service.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.SysJobDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.qo.common.SysConfigQO;

import java.util.List;

public interface SysService {
    List<SysJobDTO> listSysJob();

    SysJobDTO getSysJob(String jobCode);

    int saveSysJob(String type, SysJobDTO dto);

    int delSysJob(String jobCode);

    PageInfo<SysConfigDTO> pageSysConfig(SysConfigQO qo);

    List<SysConfigDTO> listSysConfig(SysConfigQO qo);

    int addSysConfig(SysConfigDTO dto);

    int delSysConfig(List<Integer> ids);
}
