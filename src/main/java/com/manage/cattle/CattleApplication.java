package com.manage.cattle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@MapperScan(basePackages = "com.manage.cattle.dao")
@SpringBootApplication
public class CattleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CattleApplication.class, args);
    }

}
