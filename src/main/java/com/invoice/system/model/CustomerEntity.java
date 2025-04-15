package com.invoice.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "Customer")
@Data
@ToString(exclude = "quotes")
@EqualsAndHashCode(exclude = "quotes")
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "customerName")
  private String customerName;

  @Email(message = "Enter Valid Mail")
  private String email;

  @Pattern(regexp = "[0-9]{10}", message = "Phone number must be exactly 10 digits")
  private String phone;

  @Pattern(
      regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$",
      message = "Invalid GST number")
  @Column(name = "gstNumber")
  private String gstNumber;

  @Column(name = "billingAddress")
  private String billingAddress;

  @Column(name = "shippingAddress")
  private String shippingAddress;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
  private List<QuoteEntity> quotes = new ArrayList<>();
}
