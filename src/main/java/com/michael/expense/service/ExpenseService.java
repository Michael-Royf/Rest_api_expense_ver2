package com.michael.expense.service;

import com.michael.expense.payload.request.ExpenseRequest;
import com.michael.expense.payload.response.ExpenseDto;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;

public interface ExpenseService {
    ExpenseDto createExpense(ExpenseRequest expense);

    List<ExpenseDto> getAllExpenses(Pageable pageable);

    ExpenseDto getExpenseById(Long id);

    void deleteExpenseById(Long id);

    ExpenseDto updateExpense(Long id, ExpenseRequest expenseRequest);

    List<ExpenseDto> getExpensesByCategory(String category, Pageable pageable);

    List<ExpenseDto> getExpensesByName(String name, Pageable pageable);

    List<ExpenseDto> getExpensesByNameContain(String keyword, Pageable pageable);

    List<ExpenseDto> getByDate(Date startDate, Date endDate, Pageable pageable);

}
