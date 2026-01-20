package com.example.http_lab10.repository;

import com.example.http_lab10.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByUserId(Long userId);
    Optional<Car> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndVin(Long userId, String vin);
}
