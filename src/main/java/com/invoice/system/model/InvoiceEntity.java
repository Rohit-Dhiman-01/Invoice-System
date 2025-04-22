package com.invoice.system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "Invoice")
@Data
public class InvoiceEntity {
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