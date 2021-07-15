package com.jbpark.dabang.dto;

import java.io.Serializable;

public class AddressRowDto implements Serializable {
	private static final long serialVersionUID = 6156343706172263160L;
	private String newZipcode;
	private String roadName;
	
	public String getNewZipcode() {
		return newZipcode;
	}
	public void setNewZipcode(String newZipcode) {
		this.newZipcode = newZipcode;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
}
