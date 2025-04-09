package com.invoice.system.dto;

import lombok.Data;

@Data
public class VendorDto {
    private String vendorName;
    private String email;
    private String phone;
    private String gstNumber;
    private String address;
}
