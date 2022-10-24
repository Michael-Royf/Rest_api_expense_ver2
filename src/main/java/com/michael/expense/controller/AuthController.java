package com.michael.expense.controller;

import com.michael.expense.entity.RefreshToken;
import com.michael.expense.entity.User;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserDto;
import com.michael.expense.service.RefreshTokenService;
import com.michael.expense.service.impl.UserServiceImpl;
import com.michael.expense.utility.JWTTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.michael.expense.constant.SecurityConstant.JWT_ACCESS_TOKEN_HEADER;
import static com.michael.expense.constant.SecurityConstant.JWT_REFRESH_TOKEN_HEADER;
import static com.michael.expense.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@Api(value = "Auth controller exposes registration and login REST APIs")
public class AuthController {

    private UserServiceImpl userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(UserServiceImpl userService,
                          AuthenticationManager authenticationManager,
                          JWTTokenProvider jwtTokenProvider,
                          RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @ApiOperation(value = "REST API To Login User To Expense App")
    @PostMapping("/login")
    public ResponseEntity<List<HttpStatus>> login(@RequestBody LoginRequest loginRequest) {
        authentication(loginRequest.getUsername(), loginRequest.getPassword());
        User loginUser = userService.findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + loginRequest.getUsername()));
        HttpHeaders jwtAccessAndRefreshHeader = getJwtAccessHeader(loginUser);
        return new ResponseEntity<>(jwtAccessAndRefreshHeader, HttpStatus.OK);
    }

    @ApiOperation(value = "REST API To Register User To Expense app")
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }
    @ApiOperation(value = "REST API To Verification Email")
    @GetMapping(path = "/registration/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(userService.confirmToken(token), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API To Reset Password User")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String oldPassword) {
        return new ResponseEntity<>(userService.updatePassword(oldPassword), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API To Create New Password User")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.forgotPassword(email), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API To Refresh Token")
    @PostMapping("/refresh-token")
    public ResponseEntity<List<HttpHeaders>> refreshToken(@RequestParam ("refreshtoken") String  refreshToken){
        RefreshToken refreshTokenDb = refreshTokenService.getRefreshTokenByToken(refreshToken);
        refreshTokenService.verifyExpiration(refreshToken);
        HttpHeaders newJwtAccessAndRefreshHeader = getJwtAccessHeaderAfterRefreshToken(refreshTokenDb.getUser(), refreshTokenDb);
        return new ResponseEntity<>(newJwtAccessAndRefreshHeader, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        refreshTokenService.deleteByUserId();
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }


    private void authentication(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    }

    private HttpHeaders getJwtAccessHeader(User loginUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_ACCESS_TOKEN_HEADER, jwtTokenProvider.generateJWtAccessTokenToken(loginUser));
        headers.add(JWT_REFRESH_TOKEN_HEADER, refreshTokenService.createRefreshToken(loginUser).getToken());
        return headers;
    }
    private HttpHeaders getJwtAccessHeaderAfterRefreshToken(User loginUser, RefreshToken refreshTokenDb) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_ACCESS_TOKEN_HEADER, jwtTokenProvider.generateJWtAccessTokenToken(loginUser));
        headers.add(JWT_REFRESH_TOKEN_HEADER,  refreshTokenDb.getToken());
        return headers;
    }


//    @GetMapping("/token/refresh")
//    public void refreshToken(HttpServletRequest request,
//                             HttpServletResponse response) throws IOException {
//
//        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            try {
//                String refreshToken = authorizationHeader.substring("Bearer ".length());
//                Algorithm algorithm = Algorithm.HMAC256(SecurityConstant.SECRET.getBytes());
//                JWTVerifier verifier = JWT.require(algorithm).build();
//                DecodedJWT decodedJWT = verifier.verify(refreshToken);
//                String username = decodedJWT.getSubject();
//                UserEntity user = userService.getUserByUsername(username);//email
//
//
//                List<String> list = new ArrayList<>();
//                for (ERole role : user.getRole()) {
//                    String name = role.name();
//                    list.add(name);
//                }
//                String access_token = JWT.create()
//                        .withSubject(user.getUsername())
//                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
//                        .withIssuer(request.getRequestURI().toString())
//                        .withClaim("roles", list)
//                        .sign(algorithm);
//
//                Map<String, String> tokens = new HashMap<>();
//                tokens.put("access_token", access_token);
//                tokens.put("refresh_token", refreshToken);
     //        response.setContentType(APPLICATION_JSON_VALUE);
//                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
//              //  response.setHeader("access_token", access_token);
//             //   response.setHeader("refresh_token", refresh_token);
//
//            } catch (Exception e) {
//                response.setHeader("error", e.getMessage());
//                response.setStatus(HttpStatus.FORBIDDEN.value());
//                Map<String, String> errors = new HashMap<>();
//                errors.put("error_message", e.getMessage());
//
//                response.setContentType(APPLICATION_JSON_VALUE);
//                new ObjectMapper().writeValue(response.getOutputStream(), errors);
//            }
//        } else {
//            throw new RuntimeException("Refresh token is missing");
//        }
//
//    }

}
