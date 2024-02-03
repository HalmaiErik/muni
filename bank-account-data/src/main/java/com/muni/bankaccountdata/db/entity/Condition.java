package com.muni.bankaccountdata.db.entity;

import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.entity.enums.TransactionColumn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "condition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TransactionColumn transactionColumn;

    private Operation operation;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
