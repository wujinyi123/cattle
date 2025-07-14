package com.manage.cattle.service.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dao.base.SysDao;
import com.manage.cattle.dao.base.UserDao;
import com.manage.cattle.dto.base.SysJobDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.dto.common.SysConfigDTO;
import com.manage.cattle.exception.BusinessException;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.qo.common.SysConfigQO;
import com.manage.cattle.service.base.SysService;
import com.manage.cattle.util.PermissionUtil;
import com.manage.cattle.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysServiceImpl implements SysService {
    @Resource
    private SysDao sysDao;

    @Resource
    private UserDao userDao;

    @Override
    public List<SysJobDTO> listSysJob() {
        return sysDao.listSysJob();
    }

    @Override
    public SysJobDTO getSysJob(String jobCode) {
        return sysDao.getSysJob(jobCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveSysJob(String type, SysJobDTO dto) {
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        List<SysJobDTO> existSysJobList = sysDao.listSysJob();
        List<SysJobDTO> sysJobList = "add".equals(type) ?
                existSysJobList.stream().filter(item -> item.getJobCode().equals(dto.getJobCode()) || item.getJobName().equals(dto.getJobName())).toList() :
                existSysJobList.stream().filter(item -> !item.getJobCode().equals(dto.getJobCode()) && item.getJobName().equals(dto.getJobName())).toList();
        if (sysJobList.size() > 0) {
            throw new BusinessException("岗位代码或名称已存在");
        }
        int result = "add".equals(type) ? sysDao.addSysJob(dto) : sysDao.updateSysJob(dto);
        if (result == 0) {
            throw new BusinessException("保存失败");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delSysJob(String jobCode) {
        PermissionUtil.onlySysAdmin();
        UserQO qo = new UserQO();
        qo.setJobCode(jobCode);
        List<String> usernameList = userDao.listUser(qo).stream().map(UserDTO::getUsername).toList();
        if (usernameList.size() > 0) {
            throw new BusinessException("用户(" + String.join(",", usernameList) + ")属于该岗位，请先处理用户信息");
        }
        return sysDao.delSysJob(jobCode);
    }

    @Override
    public PageInfo<SysConfigDTO> pageSysConfig(SysConfigQO qo) {
        PageHelper.startPage(qo);
        return new PageInfo<>(sysDao.listSysConfig(qo));
    }

    @Override
    public List<SysConfigDTO> listSysConfig(SysConfigQO qo) {
        return sysDao.listSysConfig(qo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addSysConfig(SysConfigDTO dto) {
        PermissionUtil.onlySysAdmin();
        String username = UserUtil.getUsername();
        dto.setCreateUser(username);
        dto.setUpdateUser(username);
        return sysDao.addSysConfig(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delSysConfig(List<Integer> ids) {
        PermissionUtil.onlySysAdmin();
        return sysDao.delSysConfig(ids);
    }
}
