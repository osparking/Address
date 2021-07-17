package com.jbpark.dabang.service;

import java.util.List;

import com.jbpark.dabang.domain.AddressRow;
import com.jbpark.dabang.main.AddrSearchKey;

public interface AddressService {
	List<AddressRow> read(AddrSearchKey addrSearchKey);
}
