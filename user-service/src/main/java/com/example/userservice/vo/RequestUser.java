package com.example.userservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "A request user object for user add")
public class RequestUser {
    @Schema(title = "사용자 이메일", description = "사용자 ID로 사용되는 이메일 정보로서, 로그인 시 사용")
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @Schema(title = "사용자 비밀번호", description = "로그인 시 사용하는 사용자 암호")
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equal or grater than 8 characters")
    private String pwd;

    @Schema(title = "사용자 이름", description = "사용자 이름")
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name not be less than two characters")
    private String name;
}