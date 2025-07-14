package com.manage.cattle.controller.base;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.manage.cattle.dto.base.UserDTO;
import com.manage.cattle.qo.base.LoginQO;
import com.manage.cattle.qo.base.UserQO;
import com.manage.cattle.service.base.UserService;
import jakarta.annotation.Resource;
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
    public UserDTO login(@RequestBody LoginQO qo) {
        return userService.login(qo);
    }

    @GetMapping("/getCurrentUser")
    public UserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/pageUser")
    public PageInfo<UserDTO> pageUser(UserQO qo) {
        return userService.pageUser(qo);
    }

    @GetMapping("/listUser")
    public List<UserDTO> listUser() {
        UserQO qo = new UserQO();
        qo.setNoExpire("true");
        return userService.listUser(qo);
    }

    @GetMapping("/getUser")
    public UserDTO getUser(@RequestParam String username) {
        return userService.getUser(username);
    }

    @PostMapping("/saveUser")
    public int saveUser(@RequestParam String type, @RequestBody UserDTO dto) {
        return userService.saveUser(type, dto);
    }

    @PostMapping("/updatePassword")
    public int updatePassword(@RequestBody JSONObject jsonObject) {
        return userService.updatePassword(jsonObject);
    }

    @PostMapping("/updatePhone")
    public int updatePhone(@RequestBody JSONObject jsonObject) {
        return userService.updatePhone(jsonObject);
    }

    @PostMapping("/delUser")
    public int delUser(@RequestBody List<String> usernameList) {
        return userService.delUser(usernameList);
    }

    @PostMapping("/reloadPassword")
    public int reloadPassword(@RequestParam String username) {
        return userService.reloadPassword(username);
    }
}
