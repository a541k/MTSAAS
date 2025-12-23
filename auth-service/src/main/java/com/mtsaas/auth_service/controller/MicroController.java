package com.mtsaas.auth_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/micro")
public class MicroController {

    @PostMapping("/string")
    public String getString(){
        return "Hello micro";
    }
}
