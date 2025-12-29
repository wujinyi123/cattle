package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.CattleChangeZoneDTO;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.CattleTransferDTO;
import com.manage.cattle.dto.base.CattleTransferReviewDTO;
import com.manage.cattle.dto.common.FileByteInfo;
import com.manage.cattle.qo.base.CattleChangeZoneQO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.CattleTransferQO;
import com.manage.cattle.qo.base.CattleTransferReviewQO;
import com.manage.cattle.service.base.CattleService;
import com.manage.cattle.util.CommonUtil;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/exportCatailDetail")
    public ResponseEntity<byte[]> exportCatailDetail(CattleQO qo) {
        FileByteInfo info = cattleService.exportCatailDetail(qo);
        return CommonUtil.responseByteArr(info);
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

    @GetMapping("/pageCattleChangeZone")
    public PageInfo<CattleChangeZoneDTO> pageCattleChangeZone(CattleChangeZoneQO qo) {
        return cattleService.pageCattleChangeZone(qo);
    }

    @PostMapping("/addCattleChangeZone")
    public int addCattleChangeZone(@RequestBody CattleChangeZoneDTO dto) {
        return cattleService.addCattleChangeZone(dto);
    }

    @GetMapping("/pageCattleTransfer")
    public PageInfo<CattleTransferDTO> pageCattleTransfer(CattleTransferQO qo) {
        return cattleService.pageCattleTransfer(qo);
    }

    @PostMapping("/addCattleTransfer")
    public int addCattleTransfer(@RequestBody CattleTransferDTO dto) {
        return cattleService.addCattleTransfer(dto);
    }

    @PostMapping("/updateCattleTransferApprover")
    public int updateCattleTransferApprover(@RequestBody CattleTransferDTO dto) {
        return cattleService.updateCattleTransferApprover(dto);
    }

    @PostMapping("/updateCattleTransferStatus")
    public int updateCattleTransferStatus(@RequestBody CattleTransferDTO dto) {
        return cattleService.updateCattleTransferStatus(dto);
    }

    @GetMapping("/getCattleTransferNum")
    public Map<?, ?> getCattleTransferNum(@RequestParam("currentFarmCode") String currentFarmCode) {
        return cattleService.getCattleTransferNum(currentFarmCode);
    }


    @GetMapping("/pageCattleTransferReview")
    public PageInfo<CattleTransferReviewDTO> pageCattleTransferReview(CattleTransferReviewQO qo) {
        return cattleService.pageCattleTransferReview(qo);
    }

    @PostMapping("/addCattleTransferReview")
    public int addCattleTransferReview(@RequestBody CattleTransferReviewDTO dto) {
        return cattleService.addCattleTransferReview(dto);
    }

    @PostMapping("/updateCattleTransferReviewApprover")
    public int updateCattleTransferReviewApprover(@RequestBody CattleTransferReviewDTO dto) {
        return cattleService.updateCattleTransferReviewApprover(dto);
    }

    @PostMapping("/updateCattleTransferReviewStatus")
    public int updateCattleTransferReviewStatus(@RequestBody CattleTransferReviewDTO dto) {
        return cattleService.updateCattleTransferReviewStatus(dto);
    }

    @GetMapping("/getCattleTransferReviewNum")
    public Map<?, ?> getCattleTransferReviewNum(@RequestParam("currentFarmCode") String currentFarmCode) {
        return cattleService.getCattleTransferReviewNum(currentFarmCode);
    }
}
