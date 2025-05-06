package com.invoice.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.invoice.system.model.base.AuditInfo;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PurchaseOrder")
@Data
public class PurchaseOrderEntity extends AuditInfo {
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
  private Double taxAmount;

  private Double totalAmount;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private PurchaseOrderStatus status;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vendor_id", referencedColumnName = "id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private VendorEntity vendor;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "quote_id", referencedColumnName = "id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private QuoteEntity quote;

  @JsonIgnore
  @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.REMOVE)
  private InvoiceEntity invoiceEntity;
}
