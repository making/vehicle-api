package com.example;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehicleController {

	private final VehicleMapper vehicleMapper;

	public VehicleController(VehicleMapper vehicleMapper) {
		this.vehicleMapper = vehicleMapper;
	}

	@GetMapping(path = "/vehicles")
	public ResponseEntity<?> getVehicles(@RequestParam(name = "name", required = false) String name) {
		final List<Vehicle> vehicles = this.vehicleMapper.findAll(name);
		return ResponseEntity.ok(vehicles);
	}

	@PostMapping(path = "/vehicles")
	public ResponseEntity<?> postVehicles(@RequestBody Vehicle vehicle) {
		final Vehicle inserted = this.vehicleMapper.insert(vehicle);
		return ResponseEntity.status(HttpStatus.CREATED).body(inserted);
	}

	@DeleteMapping(path = "/vehicles/{id}")
	public ResponseEntity<?> deleteVehicle(@PathVariable("id") Integer id) {
		this.vehicleMapper.deleteOne(id);
		return ResponseEntity.noContent().build();
	}
}
