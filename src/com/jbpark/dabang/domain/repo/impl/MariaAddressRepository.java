package com.jbpark.dabang.domain.repo.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.jbpark.dabang.domain.AddressRow;
import com.jbpark.dabang.domain.repo.AddressRepository;
import com.jbpark.dabang.main.AddrSearchKey;

@Repository
public class MariaAddressRepository implements AddressRepository {
	@Autowired
	private NamedParameterJdbcTemplate jdbcTempleate;
	
	@Override
	public List<AddressRow> read(AddrSearchKey addrSearchKey) {
		String sql = getSelectStatememt();
		
		//@formatter:off
		String sCond = null;
		
		if (addrSearchKey.get건물본번() == null) {
			// 건물명 혹은 (건물 본번 없는)도로명 
			sCond = "(B.도로명 LIKE concat(:도로_건물,'%') "
					+ "or D.시군구건물명 LIKE concat(:도로_건물, '%'))";
		} else {
			// 도로명 및 건물 본번으로 검색
			sCond = "B.도로명 LIKE concat(:도로_건물,'%') "
					+ "AND A.건물본번 LIKE concat(:건물본번, '%')";
		}
		String sqlStatement = String.format(sql, sCond);
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("도로_건물", addrSearchKey.get도로_건물());
		if (addrSearchKey.get건물본번() != null) {
			params.put("건물본번", addrSearchKey.get건물본번());
		}
		try {
			return jdbcTempleate.query(
					sqlStatement, params, new AddressRowMapper());
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getSelectStatememt() {
		return "SELECT  A.기초구역번호 AS 새우편번호, " 
				+ "concat( " 
				+ "B.시도명, ' ', " 
				+ "if (B.시군구 = '', '', concat(B.시군구,' ')), "
				+ "case when B.읍면동구분 = 0 then concat(B.읍면동,' ') " 
				+ "else ''  " + "end,  " 
				+ "concat(B.도로명,' '), "
				+ "case when A.지하여부 = 0 then ''  " 
				+ "	when A.지하여부 = 1 then '지하 '  "
				+ "	when A.지하여부 = 2 then '공중 ' end, " 
				+ "A.건물본번, " 
				+ "if (A.건물부번 = 0, '', concat('-',A.건물부번)), "
				+ "CASE WHEN (B.읍면동구분 = 0 AND D.공동주택여부 = 0) THEN '' " 
				+ "	WHEN (B.읍면동구분 = 0 AND D.공동주택여부 = 1) then "
				+ "		case D.시군구건물명  " 
				+ "			when (D.시군구건물명 = '') then ''  "
				+ "			else concat('(',D.시군구건물명,')') end  " 
				+ "	WHEN (B.읍면동구분 = 1 AND D.공동주택여부 = 0)  "
				+ "		THEN concat('(',B.읍면동,')') " 
				+ "	WHEN (B.읍면동구분 = 1 AND D.공동주택여부 = 1)  "
				+ "		THEN concat('(', B.읍면동 " 
				+ "			, case when (D.시군구건물명 = '') then ''  "
				+ "				   else concat(',', D.시군구건물명) end " 
				+ "			,')')  " + "   	END  "
				+ "   	) AS 도로명주소 " 
				+ "  FROM 도로명주소 A, 도로명코드 B, 부가정보 D  " 
				+ " WHERE A.도로명코드    = B.도로명코드 "
				+ "   AND A.읍면동일련번호 = B.읍면동일련번호 " 
				+ "   AND A.관리번호     = D.관리번호  " 
				+ "   AND %s;";
		//@formatter:on
	}

//	private static Logger logger = JLogger.getLogger(true);

	public static void main(String[] args) {
		DataSource dataSource = getMariaDbDataSource();
		var repo = new MariaAddressRepository();
		
		repo.jdbcTempleate = new NamedParameterJdbcTemplate(dataSource);

		AddrSearchKey key = new AddrSearchKey();
		key.set도로_건물("덕영대로");
		key.set건물본번("89");
		
		List<AddressRow> addresses = repo.read(key);
//		logger.config("주소 건수: " + addresses.size());
	}

	private static DataSource getMariaDbDataSource() {
		var ds = new MariaDbDataSource();

		try {
			ds.setUrl("jdbc:mariadb://localhost:3306/address_road_gg");
			ds.setUser("myself");
			ds.setPassword("1234");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}
}
