package com.michael.expense.controller;

import com.michael.expense.payload.request.ExpenseRequest;
import com.michael.expense.payload.response.ExpenseDto;
import com.michael.expense.service.impl.ExpenseServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

@Api(value = "REST APIs for Expense resources")
@CrossOrigin
@RestController
@RequestMapping("api/v1")
public class ExpenseController {

    @Autowired
    private ExpenseServiceImpl expenseService;

    @ApiOperation(value = "REST API To Create New Expense")
    @PostMapping("/expenses")
    public ResponseEntity<ExpenseDto> createNewExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        return new ResponseEntity<>(expenseService.createExpense(expenseRequest), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get All Expenses REST API")
    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses(Pageable pageable) {
        return new ResponseEntity<>(expenseService.getAllExpenses(pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Expense By Id REST API")
    @GetMapping("/expenses/{id}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable Long id) {
        return new ResponseEntity<>(expenseService.getExpenseById(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Expense By Id REST API")
    @DeleteMapping("/expenses")
    public ResponseEntity<String> deleteById(@RequestParam("id") Long id) {
        expenseService.deleteExpenseById(id);
        return new ResponseEntity<>("Expense with id " + id + " was deleted!", HttpStatus.OK);
    }

    @ApiOperation(value = "Update Expense By Id REST API")
    @PutMapping("/expenses/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable Long id,
                                                    @RequestBody @Valid ExpenseRequest expenseRequest) {
        return new ResponseEntity<>(expenseService.updateExpense(id, expenseRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Expenses By Category REST API")
    @GetMapping("/expenses/category")
    public ResponseEntity<List<ExpenseDto>> getExpensesByCategory(@RequestParam String category, Pageable pageable) {
        return new ResponseEntity<>(expenseService.getExpensesByCategory(category, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Expenses By Name REST API")
    @GetMapping("/expenses/name")
    public ResponseEntity<List<ExpenseDto>> getExpensesByName(@RequestParam String name, Pageable pageable) {
        return new ResponseEntity<>(expenseService.getExpensesByName(name, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Expenses By Name Contain Symbols REST API")
    @GetMapping("/expenses/name/contain")
    public ResponseEntity<List<ExpenseDto>> getExpensesByNameContain(@RequestParam String keyword, Pageable pageable) {
        return new ResponseEntity<>(expenseService.getExpensesByNameContain(keyword, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Expenses By Date REST API")
    @GetMapping("/expenses/date")
    public ResponseEntity<List<ExpenseDto>> getExpensesByDates(@RequestParam(required = false) Date startDate,
                                                               @RequestParam(required = false) Date endDate,
                                                               Pageable pageable) {
        return new ResponseEntity<>(expenseService.getByDate(startDate, endDate, pageable), HttpStatus.OK);
    }
}
