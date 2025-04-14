package com.invoice.system.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "quote_item")
@Data
public class QuoteItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "quote_id")
    private QuoteEntity quoteId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private ItemEntity itemId;
}
