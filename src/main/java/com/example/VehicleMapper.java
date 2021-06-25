package com.example;

import java.util.List;

public interface VehicleMapper {
	List<Vehicle> findAll();

	Vehicle insert(Vehicle vehicle);

	int deleteOne(int id);
}
