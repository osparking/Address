package com.jbpark.dabang.domain;

import java.io.Serializable;

public class AddressRow implements Serializable {

	private static final long serialVersionUID = 5343185862899786826L;
	private String newZipcode;
	private String roadName;

	public AddressRow() {
		super();
	}

	public AddressRow(String newZipcode, String roadName) {
		super();
		this.newZipcode = newZipcode;
		this.roadName = roadName;
	}

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

	@Override
	public String toString() {
		return "(우편번호)" + newZipcode + " " + roadName;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
