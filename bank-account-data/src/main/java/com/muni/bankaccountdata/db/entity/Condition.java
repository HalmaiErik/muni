package com.muni.bankaccountdata.db.entity;

import com.muni.bankaccountdata.db.entity.enums.Operation;
import com.muni.bankaccountdata.db.entity.enums.Separator;
import com.muni.bankaccountdata.db.entity.enums.TransactionColumn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    private Separator separator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Condition c)) {
            return false;
        }

        return Objects.equals(id, c.id) && Objects.equals(transactionColumn, c.transactionColumn)
                && Objects.equals(operation, c.operation) && Objects.equals(value, c.value) && Objects.equals(category, c.category)
                && Objects.equals(separator, c.separator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionColumn, operation, value, separator);
    }
}
