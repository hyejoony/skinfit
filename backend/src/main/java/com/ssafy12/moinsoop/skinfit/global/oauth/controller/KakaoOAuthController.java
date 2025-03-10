package com.ssafy12.moinsoop.skinfit.global.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy12.moinsoop.skinfit.domain.auth.dto.response.SignInResponse;
import com.ssafy12.moinsoop.skinfit.domain.auth.dto.response.TokenResponse;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.User;
import com.ssafy12.moinsoop.skinfit.global.config.RefreshTokenService;
import com.ssafy12.moinsoop.skinfit.global.oauth.dto.KakaoTokenResponse;
import com.ssafy12.moinsoop.skinfit.global.oauth.dto.KakaoUserInfo;
import com.ssafy12.moinsoop.skinfit.global.oauth.service.KaKaoOAuthService;
import com.ssafy12.moinsoop.skinfit.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthController {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final KaKaoOAuthService kakaoOAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/oauth/kakao/login")
    public ResponseEntity<Void> kakaoLogin() {
        String kakaoAuthUrl = authUri +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&prompt=login";

        return ResponseEntity.status(302)
                .location(URI.create(kakaoAuthUrl))
                .build();
    }

    @GetMapping("/login/oauth2/code/kakao")
    public void kakaoCallback(@RequestParam String code,
                                           HttpServletResponse response) throws IOException {
        log.info("인가 코드: {}", code);

        // 토큰 요청을 위한 헤더 설정
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 토큰 요청을 위한 폼 데이터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        try {
            // 카카오 토큰 받기
            ResponseEntity<KakaoTokenResponse> tokenResponse = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    new HttpEntity<>(params, tokenHeaders),
                    KakaoTokenResponse.class
            );

            if (tokenResponse.getBody() != null) {
                // 사용자 정보 요청을 위한 헤더 설정
                HttpHeaders userInfoHeaders = new HttpHeaders();
                userInfoHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                userInfoHeaders.setBearerAuth(tokenResponse.getBody().getAccessToken());

                // 사용자 정보 요청
                ResponseEntity<KakaoUserInfo> userInfoResponse = restTemplate.exchange(
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.GET,
                        new HttpEntity<>(userInfoHeaders),
                        KakaoUserInfo.class
                );

                if (userInfoResponse.getBody() != null) {
                    KakaoUserInfo kakaoUser = userInfoResponse.getBody();
                    // 회원가입 또는 로그인 처리
                    User user = kakaoOAuthService.registerOrLogin(kakaoUser);
                    boolean isRegistered = user.isRegistered();
                    log.info("로그인 성공! 유저 이메일: {}, 닉네임: {}", user.getUserEmail(), user.getNickname());


                    String jwtAccessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getRoleType());
                    String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getRoleType());

                    refreshTokenService.saveRefreshToken(user.getUserId(), jwtRefreshToken);

                    // HTTP Only 쿠키에 리프레시 토큰 설정
                    ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", jwtRefreshToken)
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(Duration.ofSeconds(604800))
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

                    // 응답에는 리프레시 토큰 제외
//                    SignInResponse signInResponse = SignInResponse.builder()
//                            .accessToken(jwtAccessToken)
//                            .isRegistered(isRegistered)
//                            .roleType(user.getRoleType())
//                            .build();

                    // 프론트엔드 리다이렉트 처리
                    String frontendRedirectUrl = "https://i12b111.p.ssafy.io/oauth/callback";

                    // URL에 파라미터 추가
                    frontendRedirectUrl += "?accessToken=" + jwtAccessToken +
                            "&isRegistered=" + isRegistered;

                    // 프론트엔드 페이지로 리다이렉트
                    response.sendRedirect(frontendRedirectUrl);

                }
            }

        } catch (Exception e) {
            response.sendRedirect("https://i12b111.p.ssafy.io/auth/login");
        }
    }

}