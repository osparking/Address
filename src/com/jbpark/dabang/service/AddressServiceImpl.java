package com.jbpark.dabang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jbpark.dabang.domain.AddressRow;
import com.jbpark.dabang.domain.repo.AddressRepository;
import com.jbpark.dabang.main.AddrSearchKey;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository addressRepository;
	
	@Override
	public List<AddressRow> read(AddrSearchKey addrSearchKey) {
		return addressRepository.read(addrSearchKey);
	}
}
