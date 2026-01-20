package com.example.http_lab10.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "cars", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "vin"}))
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Size(min = 1, max = 60)
    @Column(nullable = false, length = 60)
    private String brand;

    @NotBlank
    @Size(min = 1, max = 60)
    @Column(nullable = false, length = 60)
    private String model;

    @NotBlank
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$")
    @Column(nullable = false, length = 17)
    private String vin;

    @NotNull
    @Min(1886)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;

}
