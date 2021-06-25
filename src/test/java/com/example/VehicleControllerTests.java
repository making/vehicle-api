package com.example;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleController.class)
public class VehicleControllerTests {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	VehicleMapper vehicleMapper;

	@Test
	void getAllVehicles() throws Exception {
		given(this.vehicleMapper.findAll())
				.willReturn(List.of(new Vehicle(1, "A"), new Vehicle(2, "B")));
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
		given(this.vehicleMapper.insert(any()))
				.willReturn(new Vehicle(10, "Foo"));
		this.mockMvc.perform(post("/vehicles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Foo\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.name").value("Foo"));
	}

	@Test
	void deleteVehicle() throws Exception {
		given(this.vehicleMapper.deleteOne(1)).willReturn(1);
		this.mockMvc.perform(delete("/vehicles/1"))
				.andExpect(status().isNoContent());
		verify(this.vehicleMapper).deleteOne(1);
	}
}
