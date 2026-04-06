package com.xw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XW
 */
@SpringBootApplication
@MapperScan("com.xw.mapper")
public class DietSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietSystemBackendApplication.class, args);
    }

}
