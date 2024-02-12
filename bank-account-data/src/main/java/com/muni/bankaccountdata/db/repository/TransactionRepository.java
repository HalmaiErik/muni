package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Category;
import com.muni.bankaccountdata.db.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccount_Id(Long accountId);
    List<Transaction> findAllByAccount_IdOrderByBookingDateDesc(Long accountId);
    List<Transaction> findAllByCategoriesContains(Category category);
    boolean existsTransactionByExternalId(String externalId);
}
