package com.invoice.system.dto.mapper;

import com.invoice.system.dto.VendorDto;
import com.invoice.system.model.VendorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendorMapper {
  VendorEntity toVendorEntity(VendorDto vendorDto);

  VendorDto toVendorDto(VendorEntity vendorEntity);
}
