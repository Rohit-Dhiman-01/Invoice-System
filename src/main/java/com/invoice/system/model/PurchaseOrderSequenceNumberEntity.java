package com.invoice.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchase_order_number_generator")
@NoArgsConstructor
@Data
public class PurchaseOrderSequenceNumberEntity {
  @Id private Integer year;
  private Integer lastNumberUsed = 0;

  public PurchaseOrderSequenceNumberEntity(Integer year) {
    this.year = year;
  }
}
