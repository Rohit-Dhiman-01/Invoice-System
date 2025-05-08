package com.invoice.system.service.impl;

import com.invoice.system.config.exception.DuplicateException;
import com.invoice.system.config.exception.VendorNotFoundException;
import com.invoice.system.dto.VendorDto;
import com.invoice.system.dto.mapper.VendorMapper;
import com.invoice.system.model.VendorEntity;
import com.invoice.system.repository.VendorRepository;
import com.invoice.system.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;

    @Override
    public VendorEntity createVendor(VendorDto vendorDto) {
        if (vendorRepository.existsByEmail(vendorDto.getEmail()))
            throw new DuplicateException("Email already exists");

        if (vendorRepository.existsByPhone(vendorDto.getPhone()))
            throw new DuplicateException("Phone Number already exists");

        return vendorRepository.save(vendorMapper.toVendorEntity(vendorDto));
    }

    @Override
    public List<VendorEntity> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public VendorEntity getVendorById(Long id) {
        return vendorRepository
                .findById(id)
                .orElseThrow(() -> new VendorNotFoundException("Vendor Not Found"));
    }
}
