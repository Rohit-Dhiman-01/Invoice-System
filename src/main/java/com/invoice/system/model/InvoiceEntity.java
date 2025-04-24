package com.invoice.system.model;

import com.invoice.system.model.base.AuditInfo;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "Invoice")
@Data
public class InvoiceEntity extends AuditInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "invoiceNumber")
  private String invoiceNumber;

  @Column(name = "invoiceDate")
  private LocalDate invoiceDate;

  @Column(name = "dueDate")
  private LocalDate dueDate;

  @Column(name = "subTotal")
  private Double subTotal;

  @Column(name = "taxAmount")
  private Double taxAmount;

  @Column(name = "totalAmount")
  private Double totalAmount;

  @Column(name = "paymentStatus")
  @Enumerated(EnumType.STRING)
  private InvoiceEntityPaymentStatus paymentStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private CustomerEntity customer;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "Purchase_order_id", referencedColumnName = "id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private PurchaseOrderEntity purchaseOrder;
}
