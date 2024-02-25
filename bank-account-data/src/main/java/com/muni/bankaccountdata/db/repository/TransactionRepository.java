package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Category;
import com.muni.bankaccountdata.db.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByExternalIdAndCustomer_Id(String externalId, Long customerId);
    List<Transaction> findAllByAccount_Id(Long accountId);
    List<Transaction> findAllByAccount_IdOrderByBookingDateDesc(Long accountId);
    List<Transaction> findAllByCategoriesContains(Category category);
    List<Transaction> findAllByAccount_IdAndBookingDateAfterAndBookingDateBefore(Long accountId, LocalDate from,
                                                                                 LocalDate to);
    List<Transaction> findAllByCustomer_IdOrderByBookingDateDesc(Long customerId);
    List<Transaction> findAllByCustomer_IdAndBookingDateAfterAndBookingDateBefore(Long customerId, LocalDate from,
                                                                                  LocalDate to);
    boolean existsTransactionByExternalId(String externalId);
}
