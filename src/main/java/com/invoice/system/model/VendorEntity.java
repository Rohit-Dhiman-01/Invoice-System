package com.invoice.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "Vendor")
@Data
public class VendorEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "vendorName")
  private String vendorName;

  @Email(message = "Enter Valid Mail")
  @Column(name = "email")
  private String email;

  @Pattern(regexp = "^[0-9]{10}$", message = "Phone number Not Valid")
  @Column(name = "phone")
  private String phone;

  @Pattern(
      regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$",
      message = "Invalid GST number")
  @Column(name = "gstNumber")
  private String gstNumber;

  @Column(name = "address")
  private String address;

  @JsonIgnore
  @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PurchaseOrderEntity> purchaseOrders = new ArrayList<>();
}
