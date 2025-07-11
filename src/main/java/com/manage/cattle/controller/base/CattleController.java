package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.service.base.CattleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cattle")
public class CattleController {
    @Resource
    private CattleService cattleService;

    @GetMapping("/pageCattle")
    public PageInfo<CattleDTO> pageCattle(CattleQO qo) {
        return cattleService.pageCattle(qo);
    }

    @GetMapping("/listCattle")
    public List<CattleDTO> listCattle(CattleQO qo) {
        return cattleService.listCattle(qo);
    }

    @GetMapping("/getCattle")
    public CattleDTO getCattle(@RequestParam String cattleCode) {
        return cattleService.getCattle(cattleCode);
    }

    @PostMapping("/saveCattle")
    public int saveCattle(@RequestParam String type, @RequestBody CattleDTO dto) {
        return cattleService.saveCattle(type, dto);
    }

    @PostMapping("/delCattle")
    public int delCattle(@RequestBody List<String> cattleCodeList) {
        return cattleService.delCattle(cattleCodeList);
    }
}
