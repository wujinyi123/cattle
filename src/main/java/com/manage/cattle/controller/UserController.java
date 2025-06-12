package com.manage.cattle.controller;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.UserDTO;
import com.manage.cattle.qo.LoginQO;
import com.manage.cattle.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public UserDTO login(@RequestBody LoginQO loginQO) {
        return userService.login(loginQO);
    }

    @GetMapping("/getCurrentUser")
    public UserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/pageUser")
    public PageInfo<UserDTO> pageUser(@RequestParam("pageNum") int pageNum,
                                      @RequestParam("pageSize") int pageSize,
                                      @RequestParam(value = "username", required = false) String username,
                                      @RequestParam(value = "name", required = false) String name) {
        return userService.pageUser(pageNum, pageSize, username, name);
    }
}
