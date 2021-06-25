package com.example;

import java.util.Objects;

public class Vehicle {
	
	public Vehicle(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer id;

	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vehicle vehicle = (Vehicle) o;
		return Objects.equals(id, vehicle.id) && Objects.equals(name, vehicle.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return "Vehicle{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
