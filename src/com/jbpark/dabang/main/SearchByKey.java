package com.jbpark.dabang.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

import com.jbpark.dabang.domain.AddressRow;
import com.jbpark.utility.JLogger;

public class SearchByKey {
	private static Logger logger = JLogger.getLogger();

	private static Connection conn = getConnection();

	private static Connection getConnection() {
		Connection conn = null;
		String userName = "myself";
		String password = "1234";
		String url = "jdbc:mariadb://localhost:3306/address_road_gg";
		String driver = "org.mariadb.jdbc.Driver";

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			logger.info("Connection is successful");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void main(String[] args) {
		SearchByKey addressMan = new SearchByKey();

		Scanner scanner = new Scanner(System.in);
		while (true) {
			try {
				addressMan.search(scanner);
			} catch (StopSearchingException sse) {
				break;
			}
		}
		logger.info("종료");
		System.out.println("종료");
	}

	private void search(Scanner scanner) throws StopSearchingException {
		try {
			//@formatter:off
			String sql = "SELECT  A.기초구역번호 AS 새우편번호, " 
					+ "concat( " + "B.시도명, ' ', "
					+ "if (B.시군구 = '', '', concat(B.시군구,' ')), " 
					+ "case when B.읍면동구분 = 0 then concat(B.읍면동,' ') "
					+ "else ''  " 
					+ "end,  " 
					+ "concat(B.도로명,' '), " 
					+ "case when A.지하여부 = 0 then ''  "
					+ "	when A.지하여부 = 1 then '지하 '  " 
					+ "	when A.지하여부 = 2 then '공중 ' end, " + "A.건물본번, "
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
					+ "			,')')  " 
					+ "   	END  "
					+ "   	) AS 도로명주소 " 
					+ "  FROM 도로명주소 A, 도로명코드 B, 부가정보 D  " 
					+ " WHERE A.도로명코드    = B.도로명코드 "
					+ "   AND A.읍면동일련번호 = B.읍면동일련번호 " 
					+ "   AND A.관리번호     = D.관리번호  "
					+ "   AND %s;";
			
			AddrSearchKey addrSearchKey = getAddrSearchKey(scanner);
			
			String sCond = null;
			if (addrSearchKey.get건물본번() == null) {
				// 건물명 혹은 (건물 본번 없는)도로명 
				sCond = "(B.도로명 LIKE concat(?,'%') "
						+ "or D.시군구건물명 LIKE concat(?, '%'))";
			} else {
				// 도로명 및 건물 본번으로 검색
				sCond = "B.도로명 LIKE concat(?,'%') "
						+ "AND A.건물본번 LIKE concat(?, '%')";
			}

			String stmt = String.format(sql, sCond);
			var ps = conn.prepareStatement(stmt);

			if (addrSearchKey.get건물본번() == null) {
				ps.setString(1, addrSearchKey.get도로_건물());
				ps.setString(2, addrSearchKey.get도로_건물());
			} else {
				ps.setString(1, addrSearchKey.get도로_건물());
				ps.setString(2, addrSearchKey.get건물본번());
			}
			//@formatter:on
			logger.config(addrSearchKey.toString());
			var addressList = getAddressList(ps);
			String msg = "결과 행 수: " + addressList.size();
			logger.config(msg);
			System.out.println(msg);
			addressList.forEach(System.out::println);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private AddrSearchKey getAddrSearchKey(Scanner scanner) throws StopSearchingException {
		var asKey = new AddrSearchKey();

		System.out.println("검색 키 입력 형태 => ");
		System.out.println("\t-도로명(예, 덕영대로895)");
		System.out.println("\t-도로명 건물번호(예, 덕영대로 89)");
		System.out.println("\t-건물명(예, 세진)");
		System.out.print("(멈추려면 그냥 엔터 치세요 :-) : ");
		try {
			String inputText = null;

			if (scanner.hasNextLine()) {
				inputText = scanner.nextLine().trim();
			}
			String[] searchKeys = inputText.split("\\s+");

			assert (searchKeys.length == 1 || searchKeys.length == 2);
			if (searchKeys.length == 1) {
				// 도로명 혹은 건물명
				if (searchKeys[0].length() == 0)
					throw new StopSearchingException();
				asKey.set도로_건물(searchKeys[0]);
			} else {
				// 도로명 그리고 건물본번
				asKey.set도로_건물(searchKeys[0]);
				asKey.set건물본번(searchKeys[1]);
			}
		} catch (NoSuchElementException | IllegalStateException | NumberFormatException e) {
			System.out.println();
			throw new StopSearchingException();
		}

		return asKey;
	}

	/**
	 * 관리번호 값 도로명주소 테이블 존재여부 판단
	 * 
	 * @param ps
	 * @return 존재 때 true, 비 존재 때 false
	 */
	private List<AddressRow> getAddressList(PreparedStatement ps) {
		var roadAddrList = new ArrayList<AddressRow>();

		try (ResultSet rs = ps.executeQuery()) {
			while (rs != null && rs.next()) {
				var roadAddress = new AddressRow(rs.getString(1), rs.getString(2));
				roadAddrList.add(roadAddress);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roadAddrList;
	}
}
