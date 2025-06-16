package com.manage.cattle.util;

import com.manage.cattle.exception.BusinessException;

public class PermissionUtil {
    public static void onlySysAdmin() {
        String isSysAdmin = JWTUtil.getIsSysAdmin();
        if (!"Y".equals(isSysAdmin)) {
            throw new BusinessException("仅系统管理员操作");
        }
    }
}
