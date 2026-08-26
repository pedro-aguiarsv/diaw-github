package com.example.apiWeather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ClimaService {

    public String getClima() {

        RestClient client = RestClient.create();

        String resposta = client.get()
                .uri("https://api.open-meteo.com/v1/forecast?latitude=-25.4284&longitude=-49.2733&current=temperature_2m,relative_humidity_2m,wind_speed_10m,wind_direction_10m&daily=temperature_2m_max,temperature_2m_min&timezone=America/Sao_Paulo")
                .retrieve()
                .body(String.class);

        return resposta;
    }

}