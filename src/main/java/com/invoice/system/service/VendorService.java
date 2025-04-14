package com.invoice.system.service;

import com.invoice.system.dto.VendorDto;
import com.invoice.system.model.VendorEntity;
import java.util.List;

public interface VendorService {

  VendorEntity createVendor(VendorDto vendorDto);

  List<VendorEntity> getAllVendors();

  VendorEntity getVendorById(Long id);
}
