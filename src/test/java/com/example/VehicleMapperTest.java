package com.example;

import org.junit.jupiter.api.Test;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.datasource.url=jdbc:tc:postgresql:11:///vehicle"
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VehicleMapperTest {
	@Autowired
	VehicleMapper vehicleMapper;

	@Test
	void findAll() {
		assertThat(this.vehicleMapper.findAll(null))
				.containsExactly(new Vehicle(1, "Avalon"),
						new Vehicle(2, "Corolla"),
						new Vehicle(3, "Crown"),
						new Vehicle(4, "Levin"),
						new Vehicle(5, "Yaris"),
						new Vehicle(6, "Vios"),
						new Vehicle(7, "Glanza"),
						new Vehicle(8, "Aygo"));
	}

	@Test
	void findAllLike() {
		assertThat(this.vehicleMapper.findAll("A"))
				.containsExactly(new Vehicle(1, "Avalon"),
						new Vehicle(8, "Aygo"));
	}

	@Test
	void insert() {
		assertThat(this.vehicleMapper.insert(new Vehicle(null, "aa")))
				.isEqualTo(new Vehicle(9, "aa"));
		assertThat(this.vehicleMapper.insert(new Vehicle(null, "bb")))
				.isEqualTo(new Vehicle(10, "bb"));
	}

	@Test
	void delete() {
		assertThat(this.vehicleMapper.deleteOne(2)).isEqualTo(1);
		assertThat(this.vehicleMapper.findAll(null))
				.containsExactly(new Vehicle(1, "Avalon"),
						new Vehicle(3, "Crown"),
						new Vehicle(4, "Levin"),
						new Vehicle(5, "Yaris"),
						new Vehicle(6, "Vios"),
						new Vehicle(7, "Glanza"),
						new Vehicle(8, "Aygo"));
	}


	@Configuration
	@Import(AppConfig.class)
	static class Config {
		@Bean
		public VehicleMapper vehicleMapper(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
			return new VehicleMapper(jdbcTemplate, sqlGenerator);
		}
	}
}