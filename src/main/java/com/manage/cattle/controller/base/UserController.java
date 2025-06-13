package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.RoleDTO;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/listRole")
    public List<RoleDTO> listRole() {
        return userService.listRole();
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody LoginQO loginQO) {
        return userService.login(loginQO);
    }

    @GetMapping("/getCurrentUser")
    public UserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/pageUser")
    public PageInfo<UserDTO> pageUser(UserQO userQO) {
        return userService.pageUser(userQO);
    }
}
