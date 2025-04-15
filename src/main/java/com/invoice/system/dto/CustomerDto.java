package com.invoice.system.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerDto {
  private String customerName;
  private String email;
  private String phone;
  private String gstNumber;
  private String billingAddress;
  private String shippingAddress;
}
