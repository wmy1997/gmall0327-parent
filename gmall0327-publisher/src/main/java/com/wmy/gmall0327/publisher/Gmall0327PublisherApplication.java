package com.wmy.gmall0327.publisher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wmy.gmall0327.publisher.mapper")
public class Gmall0327PublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(Gmall0327PublisherApplication.class, args);
    }

}
