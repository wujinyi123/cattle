package com.manage.cattle.util;

import com.manage.cattle.dto.breed.BreedBaseDTO;
import com.manage.cattle.dto.breed.BreedRegisterDTO;
import com.manage.cattle.exception.BusinessException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PermissionUtil {
    public static void onlySysAdmin() {
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin)) {
            throw new BusinessException("仅系统管理员操作");
        }
    }

    public static void breedPregnancyAddPermission(BreedRegisterDTO breedRegisterDTO) {
        if (Objects.isNull(breedRegisterDTO)) {
            throw new BusinessException("登记号不存在");
        }
        Set<String> userSet = new HashSet<>();
        userSet.addAll(CommonUtil.stringToList(breedRegisterDTO.getFarmOwner()));
        userSet.addAll(CommonUtil.stringToList(breedRegisterDTO.getFarmEmployee()));
        userSet.addAll(CommonUtil.stringToList(breedRegisterDTO.getFarmAdmin()));
        String isSysAdmin = UserUtil.getIsSysAdmin();
        String username = UserUtil.getUsername();
        if (!"Y".equals(isSysAdmin) && !userSet.contains(username)) {
            throw new BusinessException("权限不足");
        }
    }

    public static void breedPregnancyDelPermission(List<? extends BreedBaseDTO> list) {
        String isSysAdmin = UserUtil.getIsSysAdmin();
        if ("Y".equals(isSysAdmin)) {
            return;
        }
        String username = UserUtil.getUsername();
        long count = list.stream().filter(item -> {
            Set<String> userSet = new HashSet<>();
            userSet.addAll(CommonUtil.stringToList(item.getFarmOwner()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmAdmin()));
            userSet.addAll(CommonUtil.stringToList(item.getFarmEmployee()));
            return !userSet.contains(username);
        }).count();
        if (count > 0L) {
            throw new BusinessException("部分权限不足");
        }
    }
}
