package com.manage.cattle.service.common;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.ImportInfo;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.qo.common.SysConfigQO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CommonService {
    PageInfo<SysConfigDTO> pageSysConfig(SysConfigQO qo);

    List<SysConfigDTO> listSysConfig(SysConfigQO qo);

    int addSysConfig(SysConfigDTO dto);

    int delSysConfig(List<Integer> ids);

    FileByteInfo exportFile(Map<String, String> params, String templateCode);

    FileByteInfo templateFile(String templateCode);

    List<String> importRequireField(String templateCode);

    ImportInfo importFile(MultipartFile file, String templateCode);
}
