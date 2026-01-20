package com.example.http_lab10.controller;

import com.example.http_lab10.model.dto.*;
import com.example.http_lab10.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarResponse> create(@Valid @RequestBody CarCreateRequest req) {
        CarResponse created = carService.create(req);
        return ResponseEntity.created(URI.create("/api/cars/" + created.getId())).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CarResponse>> listMine() {
        return ResponseEntity.ok(carService.listMine());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarResponse> getMine(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getMine(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CarResponse> updateMine(@PathVariable Long id, @Valid @RequestBody CarUpdateRequest req) {
        return ResponseEntity.ok(carService.updateMine(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMine(@PathVariable Long id) {
        carService.deleteMine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CarResponse>> search(@RequestParam String brandPrefix) {
        return ResponseEntity.ok(carService.searchMyCarsByBrandPrefix(brandPrefix));
    }
}
