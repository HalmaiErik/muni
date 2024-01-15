package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
}
