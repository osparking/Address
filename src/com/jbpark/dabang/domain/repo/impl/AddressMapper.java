package com.jbpark.dabang.domain.repo.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.jbpark.dabang.domain.AddressRow;
public class AddressMapper implements RowMapper<AddressRow> {

	@Override
	public AddressRow mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		AddressRow addressRow = new AddressRow();
		addressRow.setNewZipcode(
				Integer.toString(rs.getInt("새우편번호")));
		addressRow.setRoadName(rs.getString("도로명주소"));
		return addressRow;
	}

}
