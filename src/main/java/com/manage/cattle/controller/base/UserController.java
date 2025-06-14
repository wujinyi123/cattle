package com.manage.cattle.controller.base;

import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public PageInfo<UserDTO> pageUser(UserQO userQO) {
        return userService.pageUser(userQO);
    }

    @GetMapping("/getUser")
    public UserDTO getUser(@RequestParam("username") String username) {
        return userService.getUser(username);
    }

    @PostMapping("/saveUser")
    public int saveUser(@RequestParam("type") String type, @RequestBody UserDTO userDTO) {
        return userService.saveUser(type, userDTO);
    }

    @PostMapping("/setUserStatus")
    public int setUserStatus(@RequestParam("status") String status,
                             @RequestBody List<String> usernameList) {
        return userService.setUserStatus(status, usernameList);
    }

    @PostMapping("/resetPassword")
    public int resetPassword(@RequestBody List<String> usernameList) {
        return userService.resetPassword(usernameList);
    }

    @PostMapping("/delUser")
    public int delUser(@RequestBody List<String> usernameList) {
        return userService.delUser(usernameList);
    }
}
