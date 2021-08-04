package com.example;

import java.sql.PreparedStatement;
import java.util.List;

import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VehicleMapper {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	public VehicleMapper(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	public List<Vehicle> findAll(String name) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", name);
		final String sql = this.sqlGenerator.generate(FileLoader.load("com/example/VehicleMapper/findAll.sql"), params.getValues(), params::addValue);
		return this.jdbcTemplate.query(sql, params, (rs, i) -> new Vehicle(rs.getInt("id"), rs.getString("name")));
	}

	@Transactional
	public Vehicle insert(Vehicle vehicle) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.getJdbcTemplate().update(connection -> {
			final PreparedStatement statement = connection.prepareStatement("INSERT INTO vehicle(name) VALUES (?)", new String[] { "id" });
			statement.setString(1, vehicle.getName());
			return statement;
		}, keyHolder);
		vehicle.setId(keyHolder.getKey().intValue());
		return vehicle;
	}

	@Transactional
	public int deleteOne(int id) {
		return this.jdbcTemplate.getJdbcTemplate().update("DELETE FROM vehicle WHERE id = ?", id);
	}
}
