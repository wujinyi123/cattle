package com.manage.cattle.dao.common;

import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.qo.common.SysConfigQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonDao {
    List<SysConfigDTO> listSysConfig(SysConfigQO qo);

    int addSysConfig(SysConfigDTO dto);

    int delSysConfig(List<Integer> list);
}
