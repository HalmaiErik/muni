package com.muni.bankaccountdata.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String colorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Condition> conditions;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Category c)) {
            return false;
        }

        return Objects.equals(id, c.id) && Objects.equals(name, c.name) && Objects.equals(colorCode, c.colorCode)
                && Objects.equals(conditions, c.conditions) && Objects.equals(customer, c.customer)
                && Objects.equals(transactions, c.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, colorCode);
    }
}
