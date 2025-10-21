package com.manage.cattle.service.base.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.SysDao;
import com.manage.cattle.dao.breed.BreedDao;
import com.manage.cattle.dto.CattleBaseDTO;
import com.manage.cattle.dto.base.*;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.*;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.CattleService;
import com.manage.cattle.util.CommonUtil;
import com.manage.cattle.util.PermissionUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CattleServiceImpl implements CattleService {
    @Resource
    private CattleDao cattleDao;

    @Resource
    private FarmDao farmDao;

    @Resource
    private BreedDao breedDao;

    @Resource
    private SysDao sysDao;

    @Override
    public PageInfo<CattleDTO> pageCattle(CattleQO qo) {
        List<CattleDTO> list = cattleDao.listCattle(qo);
        setBreedInfo(list);
        list.forEach(this::setAge);
        if (StrUtil.isNotBlank(qo.getBreedStatus())) {
            list = list.stream().filter(item -> qo.getBreedStatus().equals(item.getBreedStatus())).toList();
        }
        int begin = (qo.getPageNum() - 1) * qo.getPageSize();
        int end = Math.min(qo.getPageNum() * qo.getPageSize(), list.size());
        PageInfo<CattleDTO> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(qo.getPageNum());
        pageInfo.setPageSize(qo.getPageSize());
        pageInfo.setTotal(list.size());
        pageInfo.setList(begin < end ? list.subList(begin, end) : new ArrayList<>());
        return pageInfo;
    }

    @Override
    public List<CattleDTO> listCattle(CattleQO qo) {
        List<CattleDTO> list = cattleDao.listCattle(qo);
        setBreedInfo(list);
        list.forEach(this::setAge);
        if (StrUtil.isNotBlank(qo.getBreedStatus())) {
            list = list.stream().filter(item -> qo.getBreedStatus().equals(item.getBreedStatus())).toList();
        }
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

    private void setBreedInfo(List<CattleDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> cattleCodeList = list.stream().map(CattleDTO::getCattleCode).toList();
        Map<String, BreedRegisterDTO> breedingDayMap = breedDao.listLastBreedRegister(cattleCodeList)
                .stream()
                .collect(Collectors.toMap(BreedRegisterDTO::getCattleCode,
                        Function.identity(),
                        (key1, key2) -> key2));
        List<String> checkCattleCodeList = new ArrayList<>();
        list.forEach(item -> {
            BreedRegisterDTO dto = breedingDayMap.get(item.getCattleCode());
            if (dto != null) {
                checkCattleCodeList.add(item.getCattleCode());
                item.setBreedingDay(dto.getBreedingDay());
                item.setFirstCheckDay(dto.getFirstCheckDay());
                item.setReCheckDay(dto.getReCheckDay());
                item.setExpectedDay(dto.getExpectedDay());
            }
        });
        List<BreedPregnancyCheckDTO> checkList = new ArrayList<>();
        if (checkCattleCodeList.size() > 0) {
            checkList = breedDao.listBreedPregnancyCheckByCattleCode(checkCattleCodeList);
        }
        List<BreedPregnancyResultDTO> resultList = new ArrayList<>();
        if (checkCattleCodeList.size() > 0) {
            resultList = breedDao.listBreedPregnancyResultByCattleCode(checkCattleCodeList);
        }
        String nowDate = CommonUtil.dateToStr(new Date());
        for (CattleDTO dto : list) {
            if (StrUtil.isBlank(dto.getBreedingDay())) {
                dto.setBreedStatus("空怀");
                continue;
            }
            List<BreedPregnancyCheckDTO> tempCheckList = checkList
                    .stream()
                    .filter(item -> dto.getCattleCode().equals(item.getCattleCode())
                            && item.getCheckDay().compareTo(dto.getFirstCheckDay()) >= 0
                            && item.getCheckDay().compareTo(dto.getReCheckDay()) < 0)
                    .toList();
            if (tempCheckList.size() > 0) {
                dto.setActualFirstCheckDay(tempCheckList.get(0).getCheckDay());
                dto.setFirstCheckResult(tempCheckList.get(0).getResult());
                dto.setBreedStatus("Y".equals(dto.getFirstCheckResult()) ? "初检有孕" : "初检未孕");
            } else {
                dto.setBreedStatus("已配未检");
                continue;
            }
            tempCheckList = checkList
                    .stream()
                    .filter(item -> dto.getCattleCode().equals(item.getCattleCode())
                            && item.getCheckDay().compareTo(dto.getReCheckDay()) >= 0
                            && item.getCheckDay().compareTo(dto.getExpectedDay()) < 0)
                    .toList();
            if (tempCheckList.size() > 0) {
                dto.setActualReCheckDay(tempCheckList.get(0).getCheckDay());
                dto.setReCheckResult(tempCheckList.get(0).getResult());
                dto.setBreedStatus("Y".equals(dto.getReCheckResult()) ? "复检有胎" : "复检无胎");
            }
            List<BreedPregnancyResultDTO> tempResultList = resultList
                    .stream()
                    .filter(item -> dto.getCattleCode().equals(item.getCattleCode())
                            && item.getResultDay().compareTo(dto.getReCheckDay()) >= 0
                            && item.getResultDay().compareTo(dto.getExpectedDay()) <= 0)
                    .toList();
            if (tempResultList.size() > 0) {
                dto.setBreedStatus("空怀");
                dto.setActualExpectedDay(tempResultList.get(0).getResultDay());
                dto.setExpectedResult(tempResultList.stream().map(BreedPregnancyResultDTO::getResult).distinct().sorted().collect(Collectors.joining(",")));
            }
            if (Arrays.asList(dto.getFirstCheckResult(), dto.getReCheckResult()).contains("Y") && StrUtil.isBlank(dto.getExpectedResult())) {
                LocalDate startDate = LocalDate.parse(dto.getBreedingDay());
                LocalDate endDate = LocalDate.parse(nowDate);
                long days = ChronoUnit.DAYS.between(startDate, endDate);
                dto.setPregnancyDays((int) days);
            }
        }
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
    public Map<Integer, String> importCattle(List<CattleDTO> importList) {
        FarmZoneQO farmZoneQO = new FarmZoneQO();
        farmZoneQO.setFarmCode(importList.get(0).getFarmCode());
        Map<String, FarmZoneDTO> farmZoneMap = farmDao.listFarmZone(farmZoneQO).stream().collect(Collectors.toMap(FarmZoneDTO::getFarmZoneCode,
                Function.identity()));
        SysConfigQO sysConfigQO = new SysConfigQO();
        sysConfigQO.setCode("cattleBreed");
        Map<String, String> breedMap = sysDao.listSysConfig(sysConfigQO).stream().collect(Collectors.toMap(SysConfigDTO::getValue,
                SysConfigDTO::getKey));
        Map<Integer, String> errorMap = new HashMap<>();
        for (int index = 0; index < importList.size(); index++) {
            CattleDTO dto = importList.get(index);
            if (StrUtil.isNotBlank(dto.getImportError())) {
                errorMap.put(index, dto.getImportError());
                continue;
            }
            FarmZoneDTO farmZoneDTO = farmZoneMap.get(dto.getFarmZoneCode());
            if (farmZoneDTO == null) {
                errorMap.put(index, "圈舍编号(" + dto.getFarmZoneCode() + ")不正确");
                continue;
            }
            if (cattleDao.getCattle(dto.getCattleCode()) != null) {
                errorMap.put(index, "耳牌号(" + dto.getCattleCode() + ")已存在");
                continue;
            }
            dto.setBreed(breedMap.get(dto.getBreedValue()));
            if (StrUtil.isBlank(dto.getBreed())) {
                errorMap.put(index, "品种(" + dto.getBreedValue() + ")不正确");
                continue;
            }
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(dto.getFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZoneDTO.getSize() <= cattleList.size()) {
                errorMap.put(index, "圈舍编号(" + farmZoneDTO.getFarmZoneCode() + ")牛只已满");
                continue;
            }
            int res = cattleDao.addCattle(dto);
            if (res == 0) {
                errorMap.put(index, "添加(" + dto.getCattleCode() + ")失败");
            }
        }
        return errorMap;
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

    @Override
    public PageInfo<CattleTransferReviewDTO> pageCattleTransferReview(CattleTransferReviewQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(cattleDao.listCattleTransferReview(qo));
    }

    @Override
    public int addCattleTransferReview(CattleTransferReviewDTO dto) {
        List<String> cattleCodeList = Arrays.asList(dto.getCattleCodeList().split(","));
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(cattleCodeList);
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> existCode = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        List<String> errorCattleCode = cattleCodeList.stream().filter(item -> !existCode.contains(item)).toList();
        if (!errorCattleCode.isEmpty()) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCattleCode) + ")不存在");
        }
        List<String> errorCattleFarm = cattleList.stream().filter(item -> !StrUtil.equals(dto.getOldFarmCode(), item.getFarmCode())).map(CattleDTO::getCattleCode).toList();
        if (!errorCattleFarm.isEmpty()) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCattleFarm) + ")不是当前牛场的");
        }
        List<String> workList = cattleDao.listCattleTransferReviewWork();
        Set<String> workSet = new HashSet<>();
        for (String str : workList) {
            workSet.addAll(Arrays.asList(str.split(",")));
        }
        List<String> errorCattleWork = cattleCodeList.stream().filter(workSet::contains).toList();
        if (!errorCattleWork.isEmpty()) {
            throw new BusinessException("耳牌号(" + String.join(",", errorCattleWork) + ")正在转场流程中");
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
        return cattleDao.addCattleTransferReview(dto);
    }

    @Override
    public int updateCattleTransferReviewApprover(CattleTransferReviewDTO dto) {
        if (StrUtil.isBlank(dto.getReviewId()) || StrUtil.isBlank(dto.getApprover())) {
            throw new BusinessException("流程号和评审人不能为空");
        }
        CattleTransferReviewDTO cattleTransferReview = cattleDao.getCattleTransferReview(dto.getReviewId());
        if (cattleTransferReview == null) {
            throw new BusinessException("转场流程不存在");
        }

        String username = UserUtil.getCurrentUsername();
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin) && !username.equals(cattleTransferReview.getApprover())) {
            throw new BusinessException("请当前评审人(" + cattleTransferReview.getApprover() + ")提交修改");
        }
        dto.setUpdateUser(username);
        return cattleDao.updateCattleTransferReviewApprover(dto);
    }

    @Override
    public int updateCattleTransferReviewStatus(CattleTransferReviewDTO dto) {
        if (StrUtil.isBlank(dto.getReviewId()) || StrUtil.isBlank(dto.getOpinion()) || StrUtil.isBlank(dto.getStatus())) {
            throw new BusinessException("流程号/意见/状态不能为空");
        }
        if (!List.of("取消", "完成", "拒绝").contains(dto.getStatus())) {
            throw new BusinessException("状态取值不正确");
        }
        CattleTransferReviewDTO cattleTransferReview = cattleDao.getCattleTransferReview(dto.getReviewId());
        if (cattleTransferReview == null) {
            throw new BusinessException("转场流程不存在");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setOperator(username);
        if ("取消".equals(dto.getStatus()) && !username.equals(cattleTransferReview.getSubmitUser())) {
            throw new BusinessException("流程提交人(" + cattleTransferReview.getSubmitUser() + ")才能取消");
        }
        if (List.of("完成", "拒绝").contains(dto.getStatus()) && !username.equals(cattleTransferReview.getApprover())) {
            throw new BusinessException("流程评审人(" + cattleTransferReview.getApprover() + ")才能操作");
        }
        if ("完成".equals(dto.getStatus())) {
            if (StrUtil.isBlank(dto.getNewFarmZoneCode())) {
                throw new BusinessException("新圈舍编号不能为空");
            }
            FarmZoneDTO farmZone = farmDao.getFarmZone(dto.getNewFarmZoneCode());
            if (!StrUtil.equals(cattleTransferReview.getNewFarmCode(), farmZone.getFarmCode())) {
                throw new BusinessException("圈舍编号不正确");
            }
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(dto.getNewFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZone.getSize() - cattleList.size() < cattleTransferReview.getCattleCodeList().split(",").length) {
                throw new BusinessException("圈舍" + farmZone.getFarmZoneCode() + "牛只已满");
            }
            if (cattleDao.batchTransferCattle(username, cattleTransferReview.getCattleCodeList(), dto.getNewFarmZoneCode()) == 0) {
                throw new BusinessException("转场失败");
            }
        }
        return cattleDao.updateCattleTransferReviewStatus(dto);
    }

    @Override
    public Map<?, ?> getCattleTransferReviewNum(String currentFarmCode) {
        CattleTransferReviewQO qo = new CattleTransferReviewQO();
        qo.setCurrentFarmCode(currentFarmCode);
        qo.setStatus("进行中");
        List<CattleTransferReviewDTO> list = cattleDao.listCattleTransferReview(qo);
        String username = UserUtil.getCurrentUsername();
        Map<String, Long> map = new HashMap<>();
        map.put("myCreate", list.stream().filter(item -> username.equals(item.getSubmitUser())).count());
        map.put("todo", list.stream().filter(item -> username.equals(item.getApprover())).count());
        return map;
    }
}
