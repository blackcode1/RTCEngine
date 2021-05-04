package com.example.RTCEngine;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/printer")
public class Printer {
    public String print_memory(){
        return "success";
    }
}
