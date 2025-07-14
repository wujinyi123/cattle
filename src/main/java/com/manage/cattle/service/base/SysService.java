package com.manage.cattle.service.base;

import com.manage.cattle.dto.base.SysJobDTO;

import java.util.List;

public interface SysService {
    List<SysJobDTO> listSysJob();

    SysJobDTO getSysJob(String jobCode);

    int saveSysJob(String type, SysJobDTO dto);

    int delSysJob(String jobCode);
}
