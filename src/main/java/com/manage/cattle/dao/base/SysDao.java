package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.SysJobDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDao {
    List<SysJobDTO> listSysJob();

    SysJobDTO getSysJob(@Param("jobCode") String jobCode);

    int addSysJob(SysJobDTO dto);

    int updateSysJob(SysJobDTO dto);

    int delSysJob(@Param("jobCode") String jobCode);
}
