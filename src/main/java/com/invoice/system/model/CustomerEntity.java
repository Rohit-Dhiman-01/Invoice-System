package com.invoice.system.model;


import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customerName")
    private String customerName;
    private String email;
    private String phone;
    @Column(name = "gstNumber")
    private String gstNumber;
    @Column(name = "billingAddress")
    private String billingAddress;
    @Column(name = "shippingAddress")
    private String shippingAddress;

}
