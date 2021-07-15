package com.jbpark.dabang.domain.repo.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.jbpark.dabang.domain.AddressRow;

//@formatter:off
public class AddressRowMapper implements RowMapper<AddressRow> {
	
	@Override
	public AddressRow mapRow(ResultSet rs, int rowNum) 
			throws SQLException {
		String 우편번호str = Integer.toString(rs.getInt(1));
		
		return new AddressRow(우편번호str, rs.getString(2));
	}
}
