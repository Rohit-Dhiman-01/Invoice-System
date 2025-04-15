package com.invoice.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "Quote")
@ToString(exclude = "items")
@EqualsAndHashCode(exclude = "items")
@Data
public class QuoteEntity {

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

  @Column(name = "textAmount")
  private Double textAmount;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private QuoteStatus status;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", referencedColumnName = "id")
  private CustomerEntity customer;

  @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemEntity> items = new ArrayList<>();
}
