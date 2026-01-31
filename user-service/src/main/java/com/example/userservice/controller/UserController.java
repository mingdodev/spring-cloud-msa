package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
@Tag(name = "user-controller", description = "일반 사용자 서비스를 위한 컨트롤러입니다.")
public class UserController {
    private final Environment env;
    private final Greeting greeting;
    private final UserService userService;

    @Autowired
    public UserController(Environment env, Greeting greeting, UserService userService) {
        this.env = env;
        this.greeting = greeting;
        this.userService = userService;
    }

    @Operation(summary = "Health check API", description = "Health check를 위한 API (포트 및 Token Secret 정보 확인 가능)")
    @GetMapping("/health-check")
    @Timed(value = "users.status", longTask = true)
    // longTask는 현재 작업이 얼마나 오래 돌고 있는지? 진행 중인 작업도 추적한다. 일반 타이머는 끝나야 기록됨. 느린 게 문제가 아니라 안 끝나는 게 문제인 작업을 추적할 때 사용.
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port"))
                + ", welcome message=" + env.getProperty("greeting.message")
                + ", gateway ip(env)=" + env.getProperty("gateway.ip")
                + ", token secret key=" + env.getProperty("token.secret")
                + ", token expiration time=" + env.getProperty("token.expiration-time")
                ;
    }

    @Operation(summary = "환영 메시지 출력 API", description = "Welcome message를 출력하기 위한 API")
    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome(HttpServletRequest request) {
        log.info("users.welcome ip: {}, {}, {}, {}", request.getRemoteAddr()
                , request.getRemoteHost(), request.getRequestURI(), request.getRequestURL());

        return greeting.getMessage();
    }

    @Operation(summary = "사용자 회원 가입을 위한 API", description = "user-service에 회원 가입을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @Operation(summary = "전체 사용자 목록조회 API", description = "현재 회원 가입 된 전체 사용자 목록을 조회하기 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (인증 실패 오류)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (권한이 없는 페이지에 엑세스)"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
    })
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "사용자 정보 상세조회 API", description = "사용자에 대한 상세 정보조회를 위한 API (사용자 정보 + 주문 내역 확인)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (인증 실패 오류)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (권한이 없는 페이지에 엑세스)"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND (회원 정보가 없을 겨우)"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
