package com.trip.auth.controller;

import com.trip.auth.dto.LoginUser;
import com.trip.auth.dto.LogoutUser;
import com.trip.auth.dto.RefreshToken;
import com.trip.auth.dto.AuthToken;
import com.trip.auth.service.AuthService;
import com.trip.user.controller.UserController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apiv1/auth")
public class AuthController {
    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * 사용자 계정 로그인을 합니다.
     *
     * @param req {@code LoginUser.Req} 객체에 담긴 이메일, 비밀번호
     * @return {@code LoginUser.Res} 객체에 담긴 access 토큰과 refresh 토큰을 포함하는 ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<LoginUser.Res> loginUser(@RequestBody LoginUser.Req req) {
        logger.info("AuthController-loginUser-{}", req);
        AuthToken authToken = authService.loginUser(req.getEmail(), req.getPassword());

        LoginUser.Res res = LoginUser.Res.builder()
                .accessToken(authToken.getAccessToken())
                .refreshToken(authToken.getRefreshToken())
                .build() ;

        logger.info("AuthController-loginUser-end");
        return ResponseEntity.ok(res);
    }

    /**
     * 사용자 계정을 로그아웃하고, refresh 토큰을 삭제합니다.
     *
     * @param req {@code LogoutUser.Req} 객체에 담긴 refresh 토큰
     * @return 내용이 없는 ResponseEntity
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutUser.Res> logoutUser(@RequestBody LogoutUser.Req req) {
        logger.info("AuthController-logoutUser-{}", req);
        authService.logoutUser(req.getRefreshToken());

        logger.info("AuthController-logoutUser-end");
        return ResponseEntity.noContent().build();
    }

    /**
     * access 토큰이 만료되었을 때 refresh 토큰을 통해 새로운 access 토큰을 발급받습니다.
     *
     * @param req {@code RefreshToken.Req} 객체에 담긴 refresh 토큰
     * @return {@code RefreshToken.Res} 객체에 담긴 새로 발급된 access 토큰을 포함한 ResponseEntity
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshToken.Res> refreshToken(@RequestBody RefreshToken.Req req) {
        logger.info("AuthController-refreshToken-{}", req);
        AuthToken authToken = authService.refreshAccessToken(req.getRefreshToken());

        RefreshToken.Res res = RefreshToken.Res.builder()
                .accessToken(authToken.getAccessToken())
                .build();

        logger.info("AuthController-refreshToken-end");
        return ResponseEntity.ok(res);
    }
}
