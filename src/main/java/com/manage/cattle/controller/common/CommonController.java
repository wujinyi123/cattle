package com.manage.cattle.controller.common;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.dto.common.ImportInfo;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.service.common.CommonService;
import com.manage.cattle.util.CommonUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    private CommonService commonService;

    @GetMapping("/pageSysConfig")
    public PageInfo<SysConfigDTO> pageSysConfig(@RequestParam int pageNum,
                                                @RequestParam int pageSize,
                                                @RequestParam(required = false) String code) {
        return commonService.pageSysConfig(pageNum, pageSize, code);
    }

    @GetMapping("/listSysConfig")
    public List<SysConfigDTO> listSysConfig(@RequestParam String code) {
        return commonService.listSysConfig(code);
    }

    @PostMapping("/addSysConfig")
    public int addSysConfig(@RequestBody SysConfigDTO dto) {
        return commonService.addSysConfig(dto);
    }

    @PostMapping("/delSysConfig")
    public int delSysConfig(@RequestBody List<Integer> ids) {
        return commonService.delSysConfig(ids);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(HttpServletRequest request, @RequestParam String templateCode) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (Objects.isNull(entry.getValue()) || entry.getValue().length == 0 || StringUtils.isBlank(entry.getValue()[0])) {
                continue;
            }
            params.put(entry.getKey(), entry.getValue()[0]);
        }
        return CommonUtil.responseByteArr(commonService.exportFile(params, templateCode));
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> template(@RequestParam String templateCode) {
        return CommonUtil.responseByteArr(commonService.templateFile(templateCode));
    }

    @GetMapping("/importRequireField")
    public List<String> importRequireField(@RequestParam String templateCode) {
        return commonService.importRequireField(templateCode);
    }

    @PostMapping("/importFile")
    public ImportInfo importFile(@RequestParam MultipartFile file, @RequestParam String templateCode) {
        return commonService.importFile(file, templateCode);
    }
}
