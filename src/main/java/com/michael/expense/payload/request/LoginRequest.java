package com.michael.expense.payload.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "Login request information" )
public class LoginRequest {
    @ApiModelProperty(value = "Username for authentication")
    @NotBlank(message = "Username should not be empty")
    private String username;
    @ApiModelProperty(value = "Password for authentication")
    @NotBlank(message = "Email should not be empty")
    private String password;
}
