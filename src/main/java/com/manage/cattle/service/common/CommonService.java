package com.manage.cattle.service.common;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.ImportInfo;
import com.manage.cattle.dto.common.SysConfigDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CommonService {
    PageInfo<SysConfigDTO> pageSysConfig(int pageNum, int pageSize, String code);

    List<SysConfigDTO> listSysConfig(String code);

    int addSysConfig(SysConfigDTO dto);

    int delSysConfig(List<Integer> ids);

    FileByteInfo exportFile(Map<String, String> params, String templateCode);

    FileByteInfo templateFile(String templateCode);

    List<String> importRequireField(String templateCode);

    ImportInfo importFile(MultipartFile file, String templateCode);
}
