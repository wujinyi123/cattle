package com.manage.cattle.controller.common;

import com.manage.cattle.dto.common.ExportInfo;
import com.manage.cattle.service.common.CommonService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/rest/common")
public class CommonController {
    @Resource
    private CommonService commonService;

    @GetMapping("/export")
    public void export(HttpServletRequest request, @RequestParam String templateCode) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (Objects.isNull(entry.getValue()) || entry.getValue().length == 0 || StringUtils.isBlank(entry.getValue()[0])) {
                continue;
            }
            params.put(entry.getKey(), entry.getValue()[0]);
        }
        ExportInfo exportInfo = commonService.exportFile(params, templateCode);
    }

    @GetMapping("/template")
    public void template(@RequestParam String templateCode) {
        ExportInfo exportInfo = commonService.templateFile(templateCode);
    }

    @GetMapping("/importRequireField")
    public List<String> importRequireField(@RequestParam String templateCode) {
        return commonService.importRequireField(templateCode);
    }
}
