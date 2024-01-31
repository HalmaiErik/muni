package com.muni.bankaccountdata.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    private String name;

    private String iban;

    private String currency;

    private String requisitionId;

    private LocalDate expirationDate;

    private String institutionName;

    private String institutionLogo;

    private Double balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}