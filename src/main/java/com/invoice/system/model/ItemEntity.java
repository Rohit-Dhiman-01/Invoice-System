package com.invoice.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "Item")
@Data
public class ItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "itemName")
  private String itemName;

  @Column(name = "description")
  private String description;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "rate")
  private Double rate;

  @Column(name = "hsnCode")
  @Pattern(regexp = "[0-9]{8}", message = "Invalid HSN code")
  private String hsnCode;

  @Column(name = "taxPercent")
  private Double taxPercent;

  @Column(name = "total")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Double total;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quote_id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private QuoteEntity quote;
}
