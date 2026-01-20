package com.example.http_lab10.repository;

import com.example.http_lab10.model.Car;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public CarJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Car> searchByBrandForUser(Long userId, String brandPrefix) {
        String sql = "SELECT id, user_id, brand, model, vin, year FROM cars WHERE user_id = ? AND lower(brand) LIKE lower(?)";
        String pattern = brandPrefix + "%";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Car c = new Car();
            c.setId(rs.getLong("id"));
            c.setUserId(rs.getLong("user_id"));
            c.setBrand(rs.getString("brand"));
            c.setModel(rs.getString("model"));
            c.setVin(rs.getString("vin"));
            c.setYear(rs.getInt("year"));
            return c;
        }, userId, pattern);
    }
}
