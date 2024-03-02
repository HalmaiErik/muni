package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByExternalId(String externalId);
    Optional<Account> findAccountByExternalIdAndCustomer_Id(String externalId, Long customerId);
    List<Account> findAllByCustomer_Id(Long customerId);
    List<Account> findAllByCustomer_IdOrderByInstitutionNameAsc(Long id);
}
