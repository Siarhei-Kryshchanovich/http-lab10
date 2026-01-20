package com.example.http_lab10.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 60)
    private String brand;

    @NotBlank
    @Size(min = 1, max = 60)
    private String model;

    @NotNull
    @Min(1886)
    @Max(2100)
    private Integer year;

}
