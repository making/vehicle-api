package com.example;

import java.util.List;
import java.util.Map;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintGroup;
import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validator;
import am.ik.yavi.core.ViolationMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehicleController {

	private final VehicleMapper vehicleMapper;

	private static final ConstraintGroup FOR_CREATE = ConstraintGroup.of("CREATE");

	private static final ConstraintGroup FOR_UPDATE = ConstraintGroup.of("UPDATE");

	private final Validator<Vehicle> vehicleValidator;

	public VehicleController(VehicleMapper vehicleMapper) {
		this.vehicleMapper = vehicleMapper;
		this.vehicleValidator = ValidatorBuilder.<Vehicle>of()
				.constraintOnGroup(FOR_CREATE, b -> b
						.constraint(Vehicle::getId, "id", c -> c.isNull()))
				.constraintOnGroup(FOR_UPDATE, b -> b
						.constraint(Vehicle::getId, "id", c -> c.notNull()
								.greaterThan(0)))
				.constraint(Vehicle::getName, "name", c -> c.notBlank()
						.greaterThanOrEqual(2)
						.lessThanOrEqual(100)
						.pattern("[a-zA-Z0-9]+").message("\"{0}\" must be alphanumerics")
						.predicate(name -> !vehicleMapper.findByName(name).isPresent(), ViolationMessage.of("name.unique", "\"{1}\" is already used.")))
				.build();
	}

	@GetMapping(path = "/vehicles")
	public ResponseEntity<?> getVehicles(@RequestParam(name = "_sort", defaultValue = "id") String sort,
			@RequestParam(name = "_order", defaultValue = "ASC") String order,
			@RequestParam(name = "_start", defaultValue = "0") int start,
			@RequestParam(name = "_end", defaultValue = "10") int end) {
		final List<Vehicle> vehicles = this.vehicleMapper.findAll(sort, order, start, end);
		final Long count = this.vehicleMapper.count();
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(count))
				.body(vehicles);
	}

	@GetMapping(path = "/vehicles/{id}")
	public ResponseEntity<?> getVehicle(@PathVariable("id") Integer id) {
		return ResponseEntity.of(this.vehicleMapper.findById(id));
	}

	@PostMapping(path = "/vehicles")
	public ResponseEntity<?> postVehicles(@RequestBody Vehicle vehicle) {
		return this.vehicleValidator.applicative().validate(vehicle, FOR_CREATE)
				.map(this.vehicleMapper::insert)
				.mapErrorsF(ConstraintViolation::detail)
				.fold(details -> ResponseEntity.badRequest().body(Map.of("error", "Bad Request", "details", details)),
						inserted -> ResponseEntity.status(HttpStatus.CREATED).body(inserted));

	}

	@PutMapping(path = "/vehicles/{id}")
	public ResponseEntity<?> putVehicles(@PathVariable("id") Integer id, @RequestBody Vehicle vehicle) {
		vehicle.setId(id);
		return this.vehicleValidator.applicative().validate(vehicle, FOR_UPDATE)
				.map(this.vehicleMapper::update)
				.mapErrorsF(ConstraintViolation::detail)
				.fold(details -> ResponseEntity.badRequest().body(Map.of("error", "Bad Request", "details", details)),
						updated -> ResponseEntity.ok().body(vehicle));

	}

	@DeleteMapping(path = "/vehicles/{id}")
	public ResponseEntity<?> deleteVehicle(@PathVariable("id") Integer id) {
		this.vehicleMapper.deleteOne(id);
		return ResponseEntity.ok("{}");
	}
}
