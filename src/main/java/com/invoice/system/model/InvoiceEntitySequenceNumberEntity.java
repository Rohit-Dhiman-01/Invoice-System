package com.invoice.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_number_generator")
@Data
@NoArgsConstructor
public class InvoiceEntitySequenceNumberEntity {
  @Id private Integer year;
  private Integer lastNumberUsed = 0;

  public InvoiceEntitySequenceNumberEntity(Integer year) {
    this.year = year;
  }
}
