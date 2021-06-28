package com.example;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VehicleMapper {
	private final JdbcTemplate jdbcTemplate;

	private final RowMapper<Vehicle> vehicleRowMapper = (rs, i) -> new Vehicle(rs.getInt("id"), rs.getString("name"));

	public VehicleMapper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private final Set<String> safeSort = Set.of("id", "name");

	private final Set<String> safeOrder = Set.of("ASC", "DESC");

	public List<Vehicle> findAll() {
		return this.jdbcTemplate.query("SELECT id, name FROM vehicle ORDER BY id", this.vehicleRowMapper);
	}

	public List<Vehicle> findAll(String sort, String order, int start, int end) {
		sort = safeSort.contains(sort) ? sort : "id";
		order = safeOrder.contains(order) ? order : "ASC";
		return this.jdbcTemplate.query(String.format("SELECT id, name FROM vehicle ORDER BY %s %s OFFSET %d LIMIT %d", sort, order, start, end - start), this.vehicleRowMapper);
	}

	public Optional<Vehicle> findById(int id) {
		try {
			return Optional.ofNullable(this.jdbcTemplate.queryForObject("SELECT id, name FROM vehicle WHERE id = ?", this.vehicleRowMapper, id));
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public Optional<Vehicle> findByName(String name) {
		try {
			return Optional.ofNullable(this.jdbcTemplate.queryForObject("SELECT id, name FROM vehicle WHERE name = ?", this.vehicleRowMapper, name));
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public Long count() {
		return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vehicle", Long.class);
	}

	@Transactional
	public Vehicle insert(Vehicle vehicle) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(connection -> {
			final PreparedStatement statement = connection.prepareStatement("INSERT INTO vehicle(name) VALUES (?)", new String[] { "id" });
			statement.setString(1, vehicle.getName());
			return statement;
		}, keyHolder);
		vehicle.setId(keyHolder.getKey().intValue());
		return vehicle;
	}

	@Transactional
	public int update(Vehicle vehicle) {
		return this.jdbcTemplate.update("UPDATE vehicle SET name = ? WHERE id = ?", vehicle.getName(), vehicle.getId());
	}

	@Transactional
	public int deleteOne(int id) {
		return this.jdbcTemplate.update("DELETE FROM vehicle WHERE id = ?", id);
	}
}
