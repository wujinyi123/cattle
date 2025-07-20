package com.manage.cattle.service.common;

import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.ImportInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CommonService {
    FileByteInfo exportFile(Map<String, String> params, String templateCode);

    FileByteInfo templateFile(String templateCode);

    List<String> importRequireField(String templateCode);

    ImportInfo importFile(MultipartFile file, String templateCode);

    boolean hasHelpFile();

    ResponseEntity<byte[]> downloadHelpFile();
}
