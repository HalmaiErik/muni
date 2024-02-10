package com.muni.bankaccountdata.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Account a)) {
            return false;
        }

        return Objects.equals(id, a.id) && Objects.equals(externalId, a.externalId) && Objects.equals(name, a.name)
                && Objects.equals(iban, a.iban) && Objects.equals(currency, a.currency) && Objects.equals(requisitionId, a.requisitionId)
                && Objects.equals(expirationDate, a.expirationDate) && Objects.equals(institutionName, a.institutionName)
                && Objects.equals(institutionLogo, a.institutionLogo) && Objects.equals(balance, a.balance) && Objects.equals(customer, a.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, name, iban, currency, requisitionId, expirationDate, institutionName, institutionLogo, balance);
    }
}
