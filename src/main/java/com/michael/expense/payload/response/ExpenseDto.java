package com.michael.expense.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@ApiModel(description = "Expense model information")
public class ExpenseDto {
    @ApiModelProperty(value = "Expense id")
    private Long id;
    @ApiModelProperty(value = "Expense name")
    private String name;
    @ApiModelProperty(value = "Expense description")
    private String description;
    @ApiModelProperty(value = "Expense amount")
    private BigDecimal amount;
    @ApiModelProperty(value = "Expense category")
    private String category;
    @ApiModelProperty(value = "Expense date")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private Date date;
    @ApiModelProperty(value = "Expense created date")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private Timestamp createdAt;
    @ApiModelProperty(value = "Expense update date")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private Timestamp updateAt;
}
