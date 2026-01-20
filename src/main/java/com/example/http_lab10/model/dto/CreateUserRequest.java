package com.example.http_lab10.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.http_lab10.validation.ValidUsername;
import com.example.http_lab10.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CreateUserRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    @ValidUsername
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    @ValidPassword
    private String password;

}
