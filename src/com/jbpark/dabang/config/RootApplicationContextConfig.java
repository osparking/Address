package com.jbpark.dabang.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan("com.jbpark.dabang")
public class RootApplicationContextConfig {
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(org.mariadb.jdbc.Driver.class.getName());
		ds.setUrl("jdbc:mariadb://localhost:3306/address_road_gg");
		ds.setUsername("myself");
		ds.setPassword("1234");
		return ds;
	}

	@Bean
	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource());
	}

}
