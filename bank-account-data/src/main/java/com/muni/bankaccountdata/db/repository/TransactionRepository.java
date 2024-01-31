package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccount_IdOrderByBookingDateDesc(Long accountId);
    boolean existsTransactionByExternalId(String externalId);
}
