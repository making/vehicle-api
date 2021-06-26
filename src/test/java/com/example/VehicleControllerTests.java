package com.example;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleController.class)
public class VehicleControllerTests {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getAllVehicles() throws Exception {
		this.mockMvc.perform(get("/vehicles"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("A"))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].name").value("B"));
	}

	@Test
	void postVehicle() throws Exception {
		this.mockMvc.perform(post("/vehicles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Foo\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.name").value("Foo"));
	}

	@Test
	void postVehicle_name_not_greater_than_or_equal() throws Exception {
		this.mockMvc.perform(post("/vehicles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"a\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.details.length()").value(1))
				.andExpect(jsonPath("$.details[0].defaultMessage").value("The size of \"name\" must be greater than or equal to 2. The given size is 1"));
	}

	@Test
	void postVehicle_name_not_less_than_or_equal() throws Exception {
		this.mockMvc.perform(post("/vehicles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.format("{\"name\":\"%s\"}", "a".repeat(101))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.details.length()").value(1))
				.andExpect(jsonPath("$.details[0].defaultMessage").value("The size of \"name\" must be less than or equal to 100. The given size is 101"));
	}

	@Test
	void postVehicle_name_not_alphanumerics() throws Exception {
		this.mockMvc.perform(post("/vehicles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"@@@@\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.details.length()").value(1))
				.andExpect(jsonPath("$.details[0].defaultMessage").value("\"name\" must be alphanumerics"));
	}

	@Test
	void deleteVehicle() throws Exception {
		this.mockMvc.perform(delete("/vehicles/1"))
				.andExpect(status().isNoContent());
	}

	@Configuration
	@TypeHint(types = com.jayway.jsonpath.internal.function.text.Length.class)
	static class Config {
		@Bean
		public VehicleController vehicleController(VehicleMapper vehicleMapper) {
			return new VehicleController(vehicleMapper);
		}

		@Bean
		public VehicleMapper vehicleMapper() {
			return new VehicleMapper(null) {
				@Override
				public List<Vehicle> findAll() {
					return List.of(new Vehicle(1, "A"), new Vehicle(2, "B"));
				}

				@Override
				public Vehicle insert(Vehicle vehicle) {
					return new Vehicle(10, "Foo");
				}

				@Override
				public int deleteOne(int id) {
					return 1;
				}
			};
		}
	}
}
