package com.invoice.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "PurchaseOrder")
@Data
public class PurchaseOrderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "poNumber")
  private String poNumber;

  @Column(name = "poDate")
  private LocalDate poDate;

  @Column(name = "shippingAddress")
  private String shippingAddress;

  @Column(name = "subTotal")
  private Double subTotal;

  @Column(name = "textAmount")
  private Double textAmount;

  private Double totalAmount;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private PurchaseOrderStatus status;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vendor_id", referencedColumnName = "id")
  private VendorEntity vendor;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "quote_id", referencedColumnName = "id")
  private QuoteEntity quote;
}
