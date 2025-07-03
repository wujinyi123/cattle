package com.manage.cattle.dao.common;

import com.manage.cattle.dto.common.SysConfigDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonDao {
    List<SysConfigDTO> listSysConfig(@Param("code") String code);

    int addSysConfig(SysConfigDTO dto);

    int delSysConfig(List<Integer> list);
}
