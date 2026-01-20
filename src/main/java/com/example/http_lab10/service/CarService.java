package com.example.http_lab10.service;

import com.example.http_lab10.exception.NotFoundException;
import com.example.http_lab10.model.Car;
import com.example.http_lab10.model.dto.*;
import com.example.http_lab10.repository.CarJdbcRepository;
import com.example.http_lab10.repository.CarRepository;
import com.example.http_lab10.security.AuthUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final CarJdbcRepository carJdbcRepository;
    private final AuthUser authUser;

    public CarService(CarRepository carRepository, CarJdbcRepository carJdbcRepository, AuthUser authUser) {
        this.carRepository = carRepository;
        this.carJdbcRepository = carJdbcRepository;
        this.authUser = authUser;
    }

    public CarResponse create(CarCreateRequest req) {
        Long userId = authUser.id();

        Car car = new Car();
        car.setUserId(userId);
        car.setBrand(req.getBrand());
        car.setModel(req.getModel());
        car.setVin(req.getVin());
        car.setYear(req.getYear());

        Car saved = carRepository.save(car);
        return toResponse(saved);
    }

    public List<CarResponse> listMine() {
        Long userId = authUser.id();
        return carRepository.findAllByUserId(userId).stream().map(this::toResponse).toList();
    }

    public CarResponse getMine(Long id) {
        Long userId = authUser.id();
        Car car = carRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Car not found"));
        return toResponse(car);
    }

    public CarResponse updateMine(Long id, CarUpdateRequest req) {
        Long userId = authUser.id();
        Car car = carRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Car not found"));

        car.setBrand(req.getBrand());
        car.setModel(req.getModel());
        car.setYear(req.getYear());

        Car saved = carRepository.save(car);
        return toResponse(saved);
    }

    public void deleteMine(Long id) {
        Long userId = authUser.id();
        Car car = carRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Car not found"));
        carRepository.delete(car);
    }

    public List<CarResponse> searchMyCarsByBrandPrefix(String brandPrefix) {
        Long userId = authUser.id();
        return carJdbcRepository.searchByBrandForUser(userId, brandPrefix).stream().map(this::toResponse).toList();
    }

    private CarResponse toResponse(Car c) {
        return new CarResponse(c.getId(), c.getBrand(), c.getModel(), c.getVin(), c.getYear());
    }
}
