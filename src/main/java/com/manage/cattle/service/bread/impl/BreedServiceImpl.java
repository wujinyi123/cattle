package com.manage.cattle.service.bread.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.CattleDao;
import com.manage.cattle.dao.base.FarmDao;
import com.manage.cattle.dao.base.SysDao;
import com.manage.cattle.dao.breed.BreedDao;
import com.manage.cattle.dto.base.CattleDTO;
import com.manage.cattle.dto.base.FarmZoneDTO;
import com.manage.cattle.dto.CattleBaseDTO;
import com.manage.cattle.dto.breed.BreedFrozenSemenDTO;
import com.manage.cattle.dto.breed.BreedPregnancyCheckDTO;
import com.manage.cattle.dto.breed.BreedPregnancyResultDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.CattleQO;
import com.manage.cattle.qo.breed.BreedFrozenSemenQO;
import com.manage.cattle.qo.breed.BreedPregnancyCheckQO;
import com.manage.cattle.qo.breed.BreedPregnancyResultQO;
import com.manage.cattle.qo.breed.BreedRegisterQO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.bread.BreedService;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BreedServiceImpl implements BreedService {
    @Resource
    private BreedDao breedDao;

    @Resource
    private CattleDao cattleDao;

    @Resource
    private FarmDao farmDao;

    @Resource
    private SysDao sysDao;

    @Override
    public PageInfo<BreedFrozenSemenDTO> pageBreedFrozenSemen(BreedFrozenSemenQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedFrozenSemen(qo));
    }

    @Override
    public List<BreedFrozenSemenDTO> listBreedFrozenSemen(BreedFrozenSemenQO qo) {
        return breedDao.listBreedFrozenSemen(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<Integer, String> importBreedFrozenSemen(List<BreedFrozenSemenDTO> importList) {
        Map<Integer, String> errorMap = new HashMap<>();
        SysConfigQO sysConfigQO = new SysConfigQO();
        sysConfigQO.setCode("cattleBreed");
        Map<String, String> breedMap = sysDao.listSysConfig(sysConfigQO).stream().collect(Collectors.toMap(SysConfigDTO::getValue,
                SysConfigDTO::getKey));
        for (int index = 0; index < importList.size(); index++) {
            BreedFrozenSemenDTO dto = importList.get(index);
            if (StrUtil.isNotBlank(dto.getImportError())) {
                errorMap.put(index, dto.getImportError());
                continue;
            }
            BreedFrozenSemenQO qo = new BreedFrozenSemenQO();
            qo.setFrozenSemenCode(dto.getFrozenSemenCode());
            if (breedDao.listBreedFrozenSemen(qo).size() > 0) {
                errorMap.put(index, "冻精号(" + dto.getFrozenSemenCode() + ")已存在");
                continue;
            }
            dto.setFrozenSemenBreed(breedMap.get(dto.getFrozenSemenBreedValue()));
            if (StrUtil.isBlank(dto.getFrozenSemenBreed())) {
                errorMap.put(index, "冻精品种(" + dto.getFrozenSemenBreedValue() + ")不正确");
                continue;
            }
            int res = breedDao.addBreedFrozenSemen(dto);
            if (res == 0) {
                errorMap.put(index, "添加(" + dto.getFrozenSemenCode() + ")失败");
            }
        }
        return errorMap;
    }

    @Override
    public BreedFrozenSemenDTO getBreedFrozenSemen(String frozenSemenCode) {
        return breedDao.getBreedFrozenSemen(frozenSemenCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveBreedFrozenSemen(String type, BreedFrozenSemenDTO dto) {
        BreedFrozenSemenQO qo = new BreedFrozenSemenQO();
        qo.setFrozenSemenCode(dto.getFrozenSemenCode());
        if ("add".equals(type) && breedDao.listBreedFrozenSemen(qo).size() > 0) {
            throw new BusinessException("冻精号已存在");
        }
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return "add".equals(type) ? breedDao.addBreedFrozenSemen(dto) : breedDao.updateBreedFrozenSemen(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedFrozenSemen(List<Integer> ids) {
        List<String> resCodeList = breedDao.listRefFrozenSemenCode(ids);
        if (resCodeList.size() > 0) {
            throw new BusinessException("冻精号已被引用");
        }
        return breedDao.delBreedFrozenSemen(ids);
    }

    @Override
    public PageInfo<BreedRegisterDTO> pageBreedRegister(BreedRegisterQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedRegister(qo));
    }

    @Override
    public List<BreedRegisterDTO> listBreedRegister(BreedRegisterQO qo) {
        return breedDao.listBreedRegister(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedRegister(BreedRegisterDTO dto) {
        checkCattleCode(dto);
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBreedRegister(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedRegister(List<Integer> ids) {
        return breedDao.delBreedRegister(ids);
    }

    @Override
    public PageInfo<BreedPregnancyCheckDTO> pageBreedPregnancyCheck(BreedPregnancyCheckQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedPregnancyCheck(qo));
    }

    @Override
    public List<BreedPregnancyCheckDTO> listBreedPregnancyCheck(BreedPregnancyCheckQO qo) {
        return breedDao.listBreedPregnancyCheck(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyCheck(BreedPregnancyCheckDTO dto) {
        checkCattleCode(dto);
        String username = UserUtil.getCurrentUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return breedDao.addBreedPregnancyCheck(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedPregnancyCheck(List<Integer> ids) {
        return breedDao.delBreedPregnancyCheck(ids);
    }

    @Override
    public PageInfo<BreedPregnancyResultDTO> pageBreedPregnancyResult(BreedPregnancyResultQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(breedDao.listBreedPregnancyResult(qo));
    }

    @Override
    public List<BreedPregnancyResultDTO> listBreedPregnancyResult(BreedPregnancyResultQO qo) {
        return breedDao.listBreedPregnancyResult(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addBreedPregnancyResult(BreedPregnancyResultDTO dto) {
        dto.setCattleCodeList(List.of(dto.getCattleCode()));
        checkCattleCode(dto);
        if (StrUtil.isNotBlank(dto.getChildCattleCode()) && cattleDao.getCattle(dto.getChildCattleCode()) != null) {
            throw new BusinessException("牛犊子耳牌号已存在");
        }
        String username = UserUtil.getCurrentUsername();
        if (!"死胎".equals(dto.getResult())) {
            FarmZoneDTO farmZoneDTO = farmDao.getFarmZone(dto.getChildFarmZoneCode());
            CattleQO cattleQO = new CattleQO();
            cattleQO.setFarmZoneCode(dto.getFarmZoneCode());
            List<CattleDTO> cattleList = cattleDao.listCattle(cattleQO);
            if (farmZoneDTO.getSize() <= cattleList.size()) {
                throw new BusinessException("圈舍" + farmZoneDTO.getFarmZoneCode() + "牛只已满");
            }
            CattleDTO cattleDTO = new CattleDTO();
            dto.setCreateUser(username);
            dto.setUpdateUser(username);
            cattleDTO.setFarmZoneCode(dto.getChildFarmZoneCode());
            cattleDTO.setCattleCode(dto.getChildCattleCode());
            cattleDTO.setBreed(dto.getBreed());
            cattleDTO.setSex(dto.getSex());
            cattleDTO.setColor(dto.getColor());
            cattleDTO.setBirthday(dto.getResultDay());
            cattleDTO.setSource("自产");
            if (cattleDao.addCattle(cattleDTO) == 0) {
                throw new BusinessException("添加牛犊子失败");
            }
        }
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        int res = breedDao.addBreedPregnancyResult(dto);
        if (res == 0) {
            throw new BusinessException("添加失败");
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delBreedPregnancyResult(List<Integer> ids) {
        return breedDao.delBreedPregnancyResult(ids);
    }

    @Override
    public List<BreedRegisterDTO> downloadRegisterCattle(BreedRegisterQO qo) {
        List<BreedRegisterDTO> allRegister = breedDao.listBreedRegister(qo);
        Map<String, List<BreedRegisterDTO>> groupByCattle = allRegister.stream().collect(Collectors.groupingBy(BreedRegisterDTO::getCattleCode));
        List<BreedRegisterDTO> registerList = new ArrayList<>();
        for (List<BreedRegisterDTO> value : groupByCattle.values()) {
            List<BreedRegisterDTO> temp = new ArrayList<>(value);
            temp.sort((a, b) -> b.getBreedingDay().compareTo(a.getBreedingDay()));
            registerList.add(temp.get(0));
        }
        DateTime now = DateTime.now();
        String breedingDay = now.offsetNew(DateField.DAY_OF_YEAR, -1 * qo.getOverDays()).toDateStr();
        registerList = registerList.stream().filter(item -> breedingDay.compareTo(item.getBreedingDay()) >= 0).toList();
        BreedPregnancyCheckQO breedPregnancyCheckQO = new BreedPregnancyCheckQO();
        breedPregnancyCheckQO.setFarmCode(qo.getFarmCode());
        List<BreedPregnancyCheckDTO> checkList = breedDao.listBreedPregnancyCheck(breedPregnancyCheckQO);
        for (BreedRegisterDTO dto : registerList) {
            String cattleCode = dto.getCattleCode();
            String dayStart = dto.getBreedingDay();
            String dayEnd = now.toString();
            List<String> dayList = checkList.stream()
                    .filter(item -> cattleCode.equals(item.getCattleCode())
                            && item.getCheckDay().compareTo(dayStart) >= 0
                            && item.getCheckDay().compareTo(dayEnd) <= 0)
                    .map(BreedPregnancyCheckDTO::getCheckDay)
                    .sorted()
                    .toList();
            dto.setCheckCount(dayList.size());
            dto.setCheckDays(String.join(",", dayList));
        }
        return registerList;
    }

    private void checkCattleCode(CattleBaseDTO dto) {
        if (CollectionUtils.isEmpty(dto.getCattleCodeList())) {
            throw new BusinessException("牛只耳牌号不能为空");
        }
        CattleQO qo = new CattleQO();
        qo.setCattleCodeList(dto.getCattleCodeList());
        List<CattleDTO> cattleList = cattleDao.listCattle(qo);
        List<String> existCattleCode = cattleList.stream().map(CattleDTO::getCattleCode).toList();
        String notExist = dto.getCattleCodeList().stream().filter(item -> !existCattleCode.contains(item)).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(notExist)) {
            throw new BusinessException("牛只(" + notExist + ")不存在");
        }
        String sexErr = cattleList.stream().filter(item -> !"母".equals(item.getSex())).map(CattleDTO::getCattleCode).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(sexErr)) {
            throw new BusinessException("牛只(" + sexErr + ")不是母的");
        }
        String farmErr =
                cattleList.stream().filter(item -> !StrUtil.equals(dto.getFarmCode(), item.getFarmCode())).map(CattleDTO::getCattleCode).collect(Collectors.joining(","));
        if (StrUtil.isNotBlank(farmErr)) {
            throw new BusinessException("牛只(" + farmErr + ")不是当前牧场的");
        }
    }
}
