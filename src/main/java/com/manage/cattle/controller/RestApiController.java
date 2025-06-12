package com.manage.cattle.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rest")
public class RestApiController {
    @GetMapping("/voidFunc")
    public void voidFunc() {

    }

    @GetMapping("/nullFunc")
    public Object nullFunc() {
        return null;
    }

    @GetMapping("/intFunc")
    public int intFunc() {
        return 100;
    }

    @GetMapping("/integerFunc")
    public Integer integerFunc() {
        return Integer.valueOf("100");
    }

    @GetMapping("/longFunc")
    public long longFunc() {
        return 200L;
    }

    @GetMapping("/longFunc2")
    public Long longFunc2() {
        return Long.valueOf("200");
    }

    @GetMapping("/booleanFunc")
    public boolean booleanFunc() {
        return true;
    }

    @GetMapping("/booleanFunc2")
    public Boolean booleanFunc2() {
        return Boolean.valueOf("true");
    }

    @GetMapping("/stringFunc")
    public String stringFunc() {
        return "success";
    }

    @GetMapping("/mapFunc")
    public Map<?, ?> mapFunc() {
        return Map.of("code", 1, "name", "zhangsan");
    }

    @GetMapping("/errorFunc")
    public Object errorFunc(@RequestParam(value = "str", required = false) String str) {
        if (StringUtils.isBlank(str)) {
            throw new RuntimeException("error");
        }
        return "error";
    }
}
