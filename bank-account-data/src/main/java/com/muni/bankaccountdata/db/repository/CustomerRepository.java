package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
    Optional<Customer> findCustomerByEmail(String email);
}
