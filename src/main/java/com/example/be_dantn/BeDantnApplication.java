package com.example.be_dantn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BeDantnApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeDantnApplication.class, args);

        System.out.printf("chay thanh cong!");
    }

}
