package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> getAllByCustomer_Id(Long id);
}
