package com.ford.raptorapi.repository;

import com.ford.raptorapi.model.Vehicle;
import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findByCategory(VehicleCategory category);
    List<Vehicle> findByFuelType(FuelType fuelType);
    List<Vehicle> findByIsReferenceTrue();
}
