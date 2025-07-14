package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.SysJobDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.SysService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys")
public class SysController {
    @Resource
    private SysService sysService;

    @GetMapping("/listSysJob")
    public List<SysJobDTO> listSysJob() {
        return sysService.listSysJob();
    }

    @GetMapping("/getSysJob")
    public SysJobDTO getSysJob(@RequestParam String jobCode) {
        return sysService.getSysJob(jobCode);
    }

    @PostMapping("/saveSysJob")
    public int saveSysJob(@RequestParam String type, @RequestBody SysJobDTO dto) {
        return sysService.saveSysJob(type, dto);
    }

    @PostMapping("/delSysJob")
    public int delSysJob(@RequestParam String jobCode) {
        return sysService.delSysJob(jobCode);
    }

    @GetMapping("/pageSysConfig")
    public PageInfo<SysConfigDTO> pageSysConfig(SysConfigQO qo) {
        return sysService.pageSysConfig(qo);
    }

    @GetMapping("/listSysConfig")
    public List<SysConfigDTO> listSysConfig(SysConfigQO qo) {
        return sysService.listSysConfig(qo);
    }

    @PostMapping("/addSysConfig")
    public int addSysConfig(@RequestBody SysConfigDTO dto) {
        return sysService.addSysConfig(dto);
    }

    @PostMapping("/delSysConfig")
    public int delSysConfig(@RequestBody List<Integer> ids) {
        return sysService.delSysConfig(ids);
    }
}
