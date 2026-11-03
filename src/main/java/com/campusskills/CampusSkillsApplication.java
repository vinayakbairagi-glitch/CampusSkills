package com.campusskills;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampusSkillsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSkillsApplication.class, args);
    }
}
