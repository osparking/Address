package com.jbpark.dabang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	@RequestMapping(value="read", method=RequestMethod.GET)
	public List<AddressRow> read(
			@RequestParam(required = true) String roadBldgName,
			@RequestParam(required = false) String bldgNum
			) {
		AddrSearchKey searchKey = new AddrSearchKey();
		
		searchKey.set도로_건물(roadBldgName);
		if (bldgNum != null && bldgNum.trim().length() > 0) {
			searchKey.set건물본번(bldgNum.trim());
		}
		
		return addressService.read(searchKey);
	}
}
