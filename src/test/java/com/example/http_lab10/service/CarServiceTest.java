package com.example.http_lab10.service;

import com.example.http_lab10.exception.NotFoundException;
import com.example.http_lab10.model.Car;
import com.example.http_lab10.model.dto.CarCreateRequest;
import com.example.http_lab10.repository.CarJdbcRepository;
import com.example.http_lab10.repository.CarRepository;
import com.example.http_lab10.security.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    CarRepository carRepository;

    @Mock
    CarJdbcRepository carJdbcRepository;

    @Mock
    AuthUser authUser;

    @InjectMocks
    CarService carService;

    @Test
    void create_setsOwnerFromAuthUser_andSaves() {
        when(authUser.id()).thenReturn(5L);

        CarCreateRequest req = new CarCreateRequest();
        req.setBrand("BMW");
        req.setModel("M3");
        req.setVin("VIN123");
        req.setYear(2020);

        when(carRepository.save(any(Car.class))).thenAnswer(inv -> {
            Car c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        var resp = carService.create(req);

        assertEquals(1L, resp.getId());
        assertEquals("BMW", resp.getBrand());
        assertEquals("M3", resp.getModel());
        assertEquals("VIN123", resp.getVin());
        assertEquals(2020, resp.getYear());

        ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).save(captor.capture());
        Car saved = captor.getValue();
        assertEquals(5L, saved.getUserId());
    }

    @Test
    void getMine_throwsWhenNotFound() {
        when(authUser.id()).thenReturn(5L);
        when(carRepository.findByIdAndUserId(99L, 5L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> carService.getMine(99L));
    }
}