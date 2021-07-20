package com.jbpark.dabang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jbpark.dabang.domain.AddressRow;
import com.jbpark.dabang.main.AddrSearchKey;
import com.jbpark.dabang.service.AddressService;

@RestController
@RequestMapping(value="rest")
public class AddressController {
	@Autowired
	private AddressService addressService;
	
	//@formatter:off
	@RequestMapping(value={"/{road_bldgName}",
			"/{road_bldgName}/{bldgNum}"}, 
			method=RequestMethod.GET)
	public List<AddressRow> read(
			@PathVariable(name="road_bldgName") String road_bldgName,
			@PathVariable(name="bldgNum", required = false) String bldgNum) {
		AddrSearchKey searchKey = new AddrSearchKey();
		
		searchKey.set도로_건물(road_bldgName);
		if (bldgNum != null && bldgNum.trim().length() > 0) {
			searchKey.set건물본번(bldgNum.trim());
		}
		
		return addressService.read(searchKey);
	}

}
