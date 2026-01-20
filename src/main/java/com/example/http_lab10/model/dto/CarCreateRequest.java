package com.example.http_lab10.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarCreateRequest {

    @NotBlank
    @Size(min = 1, max = 60)
    private String brand;

    @NotBlank
    @Size(min = 1, max = 60)
    private String model;

    @NotBlank
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$")
    private String vin;

    @NotNull
    @Min(1886)
    @Max(2100)
    private Integer year;

}
