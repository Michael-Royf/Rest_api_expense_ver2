package com.michael.expense.service.impl;

import com.michael.expense.entity.Expense;
import com.michael.expense.exceptions.domain.ExpenseNotFoundException;
import com.michael.expense.payload.request.ExpenseRequest;
import com.michael.expense.payload.response.ExpenseDto;
import com.michael.expense.repository.ExpenseRepository;
import com.michael.expense.service.ExpenseService;
import com.michael.expense.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    public static final String NO_EXPENSE_FOUND_BY_ID = "Expense with id %s not found";

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;


    @Override
    public ExpenseDto createExpense(ExpenseRequest expense) {

        Expense expenseEntity = Expense.builder()
                .name(expense.getName())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .date(expense.getDate())
                .user(userService.getLoggedInUser())
                .build();
        expenseEntity = expenseRepository.save(expenseEntity);
        return mapper.map(expenseEntity, ExpenseDto.class);
    }


    @Override
    public ExpenseDto getExpenseById(Long id) {
        Expense expense = getExpenseEntityById(id);
        return mapper.map(expense, ExpenseDto.class);
    }

    @Override
    public void deleteExpenseById(Long id) {
        Expense expense = getExpenseEntityById(id);
        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseDto updateExpense(Long id, ExpenseRequest expenseRequest) {
        Expense expenseDB = getExpenseEntityById(id);
        expenseDB.setName(expenseRequest.getName() != null ? expenseRequest.getName() : expenseDB.getName());
        expenseDB.setDescription(expenseRequest.getDescription() != null ? expenseRequest.getDescription() : expenseDB.getDescription());
        expenseDB.setCategory(expenseRequest.getCategory() != null ? expenseRequest.getCategory() : expenseDB.getCategory());
        expenseDB.setDate(expenseRequest.getDate() != null ? expenseRequest.getDate() : expenseDB.getDate());
        expenseDB.setAmount(expenseRequest.getAmount() != null ? expenseRequest.getAmount() : expenseDB.getAmount());
        expenseDB = expenseRepository.save(expenseDB);
        return mapper.map(expenseDB, ExpenseDto.class);
    }

    @Override
    public List<ExpenseDto> getAllExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserId(userService.getLoggedInUser().getId(), pageable);
        return getExpenseDto(expenses);
    }

    @Override
    public List<ExpenseDto> getExpensesByCategory(String category, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserIdAndCategory(
                userService.getLoggedInUser().getId(),
                category, pageable);
        return getExpenseDto(expenses);
    }

    @Override
    public List<ExpenseDto> getExpensesByName(String name, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserIdAndName(userService.getLoggedInUser().getId(),name, pageable);
        return getExpenseDto(expenses);
    }

    @Override
    public List<ExpenseDto> getExpensesByNameContain(String keyword, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserIdAndNameContaining(userService.getLoggedInUser().getId(),keyword, pageable);
        return getExpenseDto(expenses);
    }

    @Override
    public List<ExpenseDto> getByDate(Date startDate, Date endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = new Date(0);
        }
        if (endDate == null) {
            endDate = new Date(System.currentTimeMillis());
        }
        Page<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userService.getLoggedInUser().getId(),startDate, endDate, pageable);
        return getExpenseDto(expenses);
    }


    private List<ExpenseDto> getExpenseDto(Page<Expense> expenses) {
        List<Expense> expenseList = expenses.getContent();
        return expenseList
                .stream()
                .map(expense -> mapper.map(expense, ExpenseDto.class)).collect(Collectors.toList());
    }


    private Expense getExpenseEntityById(Long id) {
        return expenseRepository.findByUserIdAndId(userService.getLoggedInUser().getId(), id)
                .orElseThrow(() -> new ExpenseNotFoundException(String.format(NO_EXPENSE_FOUND_BY_ID, id)));
    }

}
