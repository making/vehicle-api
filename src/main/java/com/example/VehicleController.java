package com.example;

import java.util.List;
import java.util.Map;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehicleController {

	private final VehicleMapper vehicleMapper;

	private final Validator<Vehicle> vehicleValidator = ValidatorBuilder.<Vehicle>of()
			.constraint(Vehicle::getName, "name", c -> c.notBlank()
					.greaterThanOrEqual(2)
					.lessThanOrEqual(100)
					.pattern("[a-zA-Z0-9]+").message("\"{0}\" must be alphanumerics"))
			.build();

	public VehicleController(VehicleMapper vehicleMapper) {
		this.vehicleMapper = vehicleMapper;
	}

	@GetMapping(path = "/vehicles")
	public ResponseEntity<?> getVehicles() {
		final List<Vehicle> vehicles = this.vehicleMapper.findAll();
		return ResponseEntity.ok(vehicles);
	}

	@PostMapping(path = "/vehicles")
	public ResponseEntity<?> postVehicles(@RequestBody Vehicle vehicle) {
		return this.vehicleValidator.applicative().validate(vehicle)
				.map(this.vehicleMapper::insert)
				.mapErrorsF(ConstraintViolation::detail)
				.fold(details -> ResponseEntity.badRequest().body(Map.of("error", "Bad Request", "details", details)),
						inserted -> ResponseEntity.status(HttpStatus.CREATED).body(inserted));

	}

	@DeleteMapping(path = "/vehicles/{id}")
	public ResponseEntity<?> deleteVehicle(@PathVariable("id") Integer id) {
		this.vehicleMapper.deleteOne(id);
		return ResponseEntity.noContent().build();
	}
}
