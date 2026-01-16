package com.moneykings.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {
    @GetMapping("/hello")
    public String hello(){
        String hi = new String("hi");
        System.out.println(hi);
        return hi;
    }
}
