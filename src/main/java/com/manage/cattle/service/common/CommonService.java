package com.manage.cattle.service.common;

import com.manage.cattle.dto.common.ExportInfo;

import java.util.List;
import java.util.Map;

public interface CommonService {
    ExportInfo exportFile(Map<String, String> params, String templateCode);

    ExportInfo templateFile(String templateCode);

    List<String> importRequireField(String templateCode);
}
