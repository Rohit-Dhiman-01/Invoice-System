package com.invoice.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.invoice.system.model.base.AuditInfo;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "Quote")
@ToString(exclude = "items")
@EqualsAndHashCode(exclude = "items", callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Data
public class QuoteEntity extends AuditInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "quoteNumber")
  private String quoteNumber;

  @Column(name = "quoteDate")
  private LocalDate quoteDate;

  @Column(name = "validUntil")
  private LocalDate validUntil;

  @Column(name = "subTotal")
  private Double subTotal;

  @Column(name = "taxAmount")
  private Double taxAmount;

  private Double totalAmount;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private QuoteStatus status;

  private String currency;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private CustomerEntity customer;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "quote")
  private List<ItemEntity> items = new ArrayList<>();

  @JsonIgnore
  @OneToOne(mappedBy = "quote")
  private PurchaseOrderEntity purchaseOrder;
}
