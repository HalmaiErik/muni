package com.muni.bankaccountdata.db.repository;

import com.muni.bankaccountdata.db.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> getAllByCustomer_Id(Long id);
    Optional<Category> findCategoryByIdAndCustomer_Id(Long categoryId, Long customerId);
}
