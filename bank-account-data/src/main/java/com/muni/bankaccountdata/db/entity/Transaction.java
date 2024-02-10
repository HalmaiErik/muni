package com.muni.bankaccountdata.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    private String refFromInstitution;

    private Double amount;

    private LocalDate bookingDate;

    private String remittanceInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "transaction_category",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Transaction t)) {
            return false;
        }

        return Objects.equals(id, t.id) && Objects.equals(externalId, t.externalId)
                && Objects.equals(refFromInstitution, t.refFromInstitution) && Objects.equals(amount, t.amount)
                && Objects.equals(bookingDate, t.bookingDate) && Objects.equals(refFromInstitution, t.remittanceInfo)
                && Objects.equals(customer, t.customer) && Objects.equals(account, t.account)
                && Objects.equals(categories, t.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, refFromInstitution, amount, bookingDate, remittanceInfo);
    }

    @Override
    public String toString() {
        return id + " " + externalId;
    }
}
