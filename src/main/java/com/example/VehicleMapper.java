package com.example;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VehicleMapper {
	private final JdbcTemplate jdbcTemplate;

	public VehicleMapper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Vehicle> findAll() {
		return this.jdbcTemplate.query("SELECT id, name FROM vehicle ORDER BY id", (rs, i) -> new Vehicle(rs.getInt("id"), rs.getString("name")));
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
	public int deleteOne(int id) {
		return this.jdbcTemplate.update("DELETE FROM vehicle WHERE id = ?", id);
	}
}
