package com.example.apiWeather.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.apiWeather.service.ClimaService;

@RestController
public class ClimaController {

    private final ClimaService service;

    public ClimaController(ClimaService service) {
        this.service = service;
    }

    @GetMapping("/clima/curitiba")
    public String getClima() {
        return service.getClima();
    }
}