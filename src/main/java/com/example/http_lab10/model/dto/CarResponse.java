package com.example.http_lab10.model.dto;

import lombok.Getter;

@Getter
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private String vin;
    private Integer year;

    public CarResponse(Long id, String brand, String model, String vin, Integer year) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.vin = vin;
        this.year = year;
    }

}

