package com.example;

import java.util.List;
import java.util.Map;

import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
		final JdbcTemplate template = this.jdbcTemplate.getJdbcTemplate();
		final Integer id = template.queryForObject("SELECT MAX(id) + 1 FROM vehicle", Integer.class); // TODO sequence
		this.jdbcTemplate.update("INSERT INTO vehicle(id, name) VALUES(:id, :name)", Map.of("id", id, "name", vehicle.getName()));
		vehicle.setId(id);
		return vehicle;
	}

	@Transactional
	public int deleteOne(int id) {
		return this.jdbcTemplate.getJdbcTemplate().update("DELETE FROM vehicle WHERE id = ?", id);
	}
}
