package com.michael.expense.payload.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
@ApiModel(description = "Expense request information")
public class ExpenseRequest {
        @NotBlank(message = "Expense name must not be null")
        @Size(min = 3, message = "Expense name must be atleast 3 characters")
        @ApiModelProperty(value = "Expense name")
        private String name;
        @ApiModelProperty(value = "Expense description")
        @NotNull(message = "Expense description should not be null")
        private String description;
        @ApiModelProperty(value = "Expense amount")
        @NotNull(message = "Expense amount should not be null")
        private BigDecimal amount;
        @ApiModelProperty(value = "Expense category")
        @NotBlank(message = "Category should not be null")
        private String category;
        @ApiModelProperty(value = "Expense date")
        @NotNull(message = "Date must not be null")
        private Date date;
}
