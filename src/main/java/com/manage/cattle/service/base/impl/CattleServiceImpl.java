package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.SysDao;
import com.manage.cattle.dto.base.CattleChangeZoneDTO;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.CattleTransferDTO;
import com.manage.cattle.dto.base.FarmDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleChangeZoneQO;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.base.CattleTransferQO;
import com.manage.cattle.qo.base.FarmZoneQO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.CattleService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.PermissionUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CattleServiceImpl implements CattleService {
    @Resource
    private CattleDao cattleDao;

    @Resource
    private FarmDao farmDao;

    @Resource
    private SysDao sysDao;

    @Override
    public PageInfo<CattleDTO> pageCattle(CattleQO qo) {
        PageHelper.startPage(qo);
        PageInfo<CattleDTO> pageInfo = new PageInfo<>(cattleDao.listCattle(qo));
        pageInfo.getList().forEach(this::setAge);
        return pageInfo;
    }

    @Override
    public List<CattleDTO> listCattle(CattleQO qo) {
        List<CattleDTO> list = cattleDao.listCattle(qo);
        list.forEach(this::setAge);
        return list;
    }

    @Override
    public CattleDTO getCattle(String cattleCode) {
        CattleDTO dto = cattleDao.getCattle(cattleCode);
        if (dto == null) {
            throw new BusinessException("牛只不存在");
        }
        setAge(dto);
        return dto;
    }

    private void setAge(CattleDTO dto) {
        if (dto == null) {
            return;
        }
        String start = dto.getBirthday();
        String end = CommonUtil.dateToStr(new Date());
        if (StrUtil.equals(start, end)) {
            dto.setAge("0天");
            return;
        }
        // 将字符串转换为LocalDate对象
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        // 计算两个日期之间的差异
        Period period = Period.between(startDate, endDate);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        String age = "";
        if (years != 0) {
            age += years + "年";
        }
        if (months != 0) {
            age += months + "月";
        }
        if (days != 0) {
            age += days + "天";
        }
        dto.setAge(age);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveCattle(String type, CattleDTO dto) {
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getFarmZoneCode());
        if (Objects.isNull(farmZoneDTO)) {
            throw new BusinessException("圈舍编号不存在");
        }
        if ("add".equals(type)) {
            if (cattleDao.getCattle(dto.getCattleCode()) != null) {
                throw new BusinessException("耳牌号已存在");
            } else {
                CattleQO cattleQO = new CattleQO();
                cattleQO.setFarmZoneCode(farmZoneDTO.getFarmZoneCode());
                List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
                if (farmZoneDTO.getSize() <= cattleList.size()) {
                    throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
                }
            }
        }
        int result = "add".equals(type) ? cattleDao.addCattle(dto) : cattleDao.updateCattle(dto);
        if (result == 0) {
            throw new BusinessException("保存失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delCattle(List<String> cattleCodeList) {
        PermissionUtil.onlySysAdmin();
        return cattleDao.delCattle(cattleCodeList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> importCattle(List<CattleDTO> importList) {
        FarmZoneQO farmZoneQO = new FarmZoneQO();
        farmZoneQO.setFarmCode(importList.get(0).getFarmCode());
        Map<String, FarmZoneDTO> farmZoneMap = farmDao.listFarmZone(farmZoneQO).stream().collect(Collectors.toMap(FarmZoneDTO::getFarmZoneCode,
                Function.identity()));
        SysConfigQO sysConfigQO = new SysConfigQO();
        sysConfigQO.setCode("cattleBreed");
        Map<String, String> breedMap = sysDao.listSysConfig(sysConfigQO).stream().collect(Collectors.toMap(SysConfigDTO::getValue,
                SysConfigDTO::getKey));
        List<String> errorList = new ArrayList<>();
        for (CattleDTO dto : importList) {
            if (StrUtil.isNotBlank(dto.getImportError())) {
                errorList.add(dto.getImportError());
                continue;
            }
            FarmZoneDTO farmZoneDTO = farmZoneMap.get(dto.getFarmZoneCode());
            if (farmZoneDTO == null) {
                errorList.add("圈舍编号不正确");
                continue;
            }
            if (cattleDao.getCattle(dto.getCattleCode()) != null) {
                errorList.add("耳牌号(" + dto.getCattleCode() + ")已存在");
                continue;
            }
            dto.setBreed(breedMap.get(dto.getBreedValue()));
            if (StrUtil.isBlank(dto.getBreed())) {
                errorList.add("品种不正确");
                continue;
            }
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(dto.getFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZoneDTO.getSize() <= cattleList.size()) {
                throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
            }
            int res = cattleDao.addCattle(dto);
            if (res == 0) {
                errorList.add("添加失败");
            }
        }
        return errorList;
    }

    @Override
    public PageInfo<CattleChangeZoneDTO> pageCattleChangeZone(CattleChangeZoneQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(cattleDao.listCattleChangeZone(qo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addCattleChangeZone(CattleChangeZoneDTO dto) {
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        if (cattleDao.updateCattleZone(dto) == 0) {
            throw new BusinessException("转舍失败");
        }
        return cattleDao.addCattleChangeZone(dto);
    }

    @Override
    public PageInfo<CattleTransferDTO> pageCattleTransfer(CattleTransferQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(cattleDao.listCattleTransfer(qo));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addCattleTransfer(CattleTransferDTO dto) {
        if (cattleDao.countCattleTransferWork(dto.getOldCattleCode()) > 0) {
            throw new BusinessException("该牛只存在转场审批流程中");
        }
        CattleDTO cattle = cattleDao.getCattle(dto.getOldCattleCode());
        if (cattle == null) {
            throw new BusinessException("原牛只耳牌号不正确");
        }
        if (!StrUtil.equals(dto.getOldFarmCode(), cattle.getFarmCode())) {
            throw new BusinessException("请输入当前牛场的牛只耳牌号");
        }
        String username = UserUtil.getCurrentUsername();
        String isSysAdmin = UserUtil.getIsSysAdmin();
        FarmDTO oldFarm = farmDao.getFarm(dto.getOldFarmCode());
        if (!"Y".equals(isSysAdmin) && !username.equals(oldFarm.getFarmOwner())) {
            throw new BusinessException("请输入当前牛场负责人(" + oldFarm.getFarmOwner() + ")提交");
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        dto.setSubmitUser(username);
        FarmDTO newFarm = farmDao.getFarm(dto.getNewFarmCode());
        dto.setApprover(newFarm.getFarmOwner());
        dto.setStatus("进行中");
        dto.setReviewId(String.valueOf(System.currentTimeMillis()));
        return cattleDao.addCattleTransfer(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateCattleTransferApprover(CattleTransferDTO dto) {
        if (StrUtil.isBlank(dto.getReviewId()) || StrUtil.isBlank(dto.getApprover())) {
            throw new BusinessException("流程号和评审人不能为空");
        }
        CattleTransferDTO cattleTransfer = cattleDao.getCattleTransfer(dto.getReviewId());
        if (cattleTransfer == null) {
            throw new BusinessException("转场流程不存在");
        }

        String username = UserUtil.getCurrentUsername();
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !username.equals(cattleTransfer.getApprover())) {
            throw new BusinessException("请当前评审人(" + cattleTransfer.getApprover() + ")提交修改");
        }
        dto.setUpdateUser(username);
        return cattleDao.updateCattleTransferApprover(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateCattleTransferStatus(CattleTransferDTO dto) {
        if (StrUtil.isBlank(dto.getReviewId()) || StrUtil.isBlank(dto.getOpinion()) || StrUtil.isBlank(dto.getStatus())) {
            throw new BusinessException("流程号/意见/状态不能为空");
        }
        if (!List.of("取消", "完成", "拒绝").contains(dto.getStatus())) {
            throw new BusinessException("状态取值不正确");
        }
        CattleTransferDTO cattleTransfer = cattleDao.getCattleTransfer(dto.getReviewId());
        if (cattleTransfer == null) {
            throw new BusinessException("转场流程不存在");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setOperator(username);
        if ("取消".equals(dto.getStatus()) && !username.equals(cattleTransfer.getSubmitUser())) {
            throw new BusinessException("流程提交人(" + cattleTransfer.getSubmitUser() + ")才能取消");
        }
        if (List.of("完成", "拒绝").contains(dto.getStatus()) && !username.equals(cattleTransfer.getApprover())) {
            throw new BusinessException("流程评审人(" + cattleTransfer.getApprover() + ")才能操作");
        }
        if ("完成".equals(dto.getStatus())) {
            if (StrUtil.isBlank(dto.getNewFarmZoneCode()) || StrUtil.isBlank(dto.getNewCattleCode())) {
                throw new BusinessException("新圈舍编号/新耳牌号不能为空");
            }
            FarmZoneDTO farmZone = farmDao.getFarmZone(dto.getNewFarmZoneCode());
            if (!StrUtil.equals(cattleTransfer.getNewFarmCode(), farmZone.getFarmCode())) {
                throw new BusinessException("圈舍编号不正确");
            }
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(dto.getNewFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZone.getSize() <= cattleList.size()) {
                throw new BusinessException("圈舍" + farmZone.getFarmZoneCode() + "牛只已满");
            }
            CattleDTO cattle = cattleDao.getCattle(cattleTransfer.getOldCattleCode());
            if (cattle == null) {
                throw new BusinessException("牛只不存在");
            }
            dto.setOldCattleInfo(JSONUtil.toJsonStr(cattle));
            cattle.setUpdateUser(username);
            cattle.setFarmZoneCode(dto.getNewFarmZoneCode());
            cattle.setCattleCode(dto.getNewCattleCode());
            if (cattleDao.transferCattle(cattle) == 0) {
                throw new BusinessException("转场失败");
            }
            cattle = cattleDao.getCattle(cattle.getCattleCode());
            dto.setNewCattleInfo(JSONUtil.toJsonStr(cattle));
        }
        return cattleDao.updateCattleTransferStatus(dto);
    }

    @Override
    public Map<?, ?> getCattleTransferNum(String currentFarmCode) {
        CattleTransferQO qo = new CattleTransferQO();
        qo.setCurrentFarmCode(currentFarmCode);
        qo.setStatus("进行中");
        List<CattleTransferDTO> list = cattleDao.listCattleTransfer(qo);
        String username = UserUtil.getCurrentUsername();
        Map<String, Long> map = new HashMap<>();
        map.put("myCreate", list.stream().filter(item -> username.equals(item.getSubmitUser())).count());
        map.put("todo", list.stream().filter(item -> username.equals(item.getApprover())).count());
        return map;
    }
}
