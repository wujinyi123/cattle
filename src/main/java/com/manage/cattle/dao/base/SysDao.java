package com.manage.cattle.dao.base;

import com.manage.cattle.dto.base.SysJobDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.qo.common.SysConfigQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDao {
    List<SysJobDTO> listSysJob();

    SysJobDTO getSysJob(@Param("jobCode") String jobCode);

    int addSysJob(SysJobDTO dto);

    int updateSysJob(SysJobDTO dto);

    int delSysJob(@Param("jobCode") String jobCode);

    List<SysConfigDTO> listSysConfig(SysConfigQO qo);

    int addSysConfig(SysConfigDTO dto);

    int delSysConfig(List<Integer> list);
}
