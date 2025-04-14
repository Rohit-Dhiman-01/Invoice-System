package com.invoice.system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Quote")
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;

    @OneToMany
    @JoinTable(
            name = "quote_item",
            joinColumns = @JoinColumn(name = "quote_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<ItemEntity> items = new ArrayList<>();
}
