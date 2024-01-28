package com.muni.bankaccountdata.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<Account> accounts;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<Category> categories;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<Rule> rules;
}
