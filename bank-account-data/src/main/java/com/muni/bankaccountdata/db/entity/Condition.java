package com.muni.bankaccountdata.db.entity;

import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.entity.enums.TransactionColumn;
import jakarta.persistence.*;

@Entity
@Table(name = "condition")
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TransactionColumn transactionColumn;

    private Operation operation;

    private String value;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;
}
