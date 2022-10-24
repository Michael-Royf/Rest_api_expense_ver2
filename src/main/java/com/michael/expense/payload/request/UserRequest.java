package com.michael.expense.payload.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@ApiModel(description = "User request information")
public class UserRequest {
    @NotBlank(message = "Username should not be empty")
    @ApiModelProperty(value = "Username for user")
    private String username;
    @NotBlank(message = "First name should not be empty")
    @ApiModelProperty(value = "First name for user")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    @ApiModelProperty(value = "Last name for user")
    private String lastName;
    @Email
    @NotBlank(message = "Email should not be empty")
    @ApiModelProperty(value = "Email for user")
    private String email;
}
