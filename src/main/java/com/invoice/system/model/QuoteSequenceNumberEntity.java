package com.invoice.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@Entity
@Table(name = "quote_number_generator")
@NoArgsConstructor
@Data
public class QuoteSequenceNumberEntity {
    @Id
    private Integer year;
    private Integer lastNumberUsed =0;

    public QuoteSequenceNumberEntity(Integer year) {
        this.year = year;
    }


}
