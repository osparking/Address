package com.jbpark.dabang.domain.repo;

import java.util.List;

import com.jbpark.dabang.domain.AddressRow;
import com.jbpark.dabang.main.AddrSearchKey;

public interface AddressRepository {
	List<AddressRow> read(AddrSearchKey addrSearchKey);
}
