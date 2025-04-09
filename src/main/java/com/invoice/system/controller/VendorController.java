package com.invoice.system.controller;

import com.invoice.system.dto.VendorDto;
import com.invoice.system.model.VendorEntity;
import com.invoice.system.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendors")
@RequiredArgsConstructor
public class VendorController {

  private final VendorService vendorService;

  @PostMapping
  public ResponseEntity<VendorEntity> createVendor(@Valid @RequestBody VendorDto vendorDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(vendorService.createVendor(vendorDto));
  }

  @GetMapping
  public ResponseEntity<List<VendorEntity>> getAllVendors() {
    return ResponseEntity.ok(vendorService.getAllVendors());
  }

  @GetMapping("/{id}")
  public ResponseEntity<VendorEntity> getVendorById(@PathVariable Long id){
    return ResponseEntity.ok(vendorService.getVendorById(id));
  }
}
