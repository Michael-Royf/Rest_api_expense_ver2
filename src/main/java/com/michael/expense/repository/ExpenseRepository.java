package com.michael.expense.repository;

import com.michael.expense.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    //select * from tbl_expenses where category = ?
    Page<Expense> findByUserIdAndCategory(Long userId, String category, Pageable pageable);


    Page<Expense> findByUserIdAndName(Long userId, String name, Pageable pageable);

    //select * from tbl_expenses where name like %keyword%'
    Page<Expense> findByUserIdAndNameContaining(Long userId, String keyword, Pageable pageable);


    //select * from tbl_expenses  where date between  'startDate' and 'endDate'
    Page<Expense> findByUserIdAndDateBetween(Long userId, Date startDate, Date endDate, Pageable pageable);


    Page<Expense> findByUserId(Long userId, Pageable pageable);

    Optional<Expense> findByUserIdAndId(Long userId, Long expenseId);


}
