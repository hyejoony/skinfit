package com.ssafy12.moinsoop.skinfit.domain.user.controller;

import com.ssafy12.moinsoop.skinfit.domain.user.dto.request.EmailVerificationRequest;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.request.RegisterUserInfoRequest;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.request.SignUpRequest;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.request.UserEmailRequest;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.response.UserNicknameAndUserSkinTypeResponse;
import com.ssafy12.moinsoop.skinfit.domain.user.service.MyPageService;
import com.ssafy12.moinsoop.skinfit.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MyPageService myPageService;

    // 이메일 중복확인
    @PostMapping("/email-duplicate")
    public ResponseEntity<String> checkDuplicateUserEmail(@RequestBody UserEmailRequest request) {
        userService.checkDuplicateUserEmail(request.getUserEmail());
        return ResponseEntity.ok("가입할 수 있는 이메일입니다.");
    }

    // 이메일 보내기
    @PostMapping("/email-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody UserEmailRequest request) {
        userService.sendVerificationEmail(request.getUserEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.ok("회원가입에 성공했습니다!");
    }

    // 비밀번호 분실 시 임시비밀번호 메일로 보내기
    @PostMapping("password-temporary")
    public ResponseEntity<String> sendTemporaryPassword(@RequestBody UserEmailRequest request) {
        userService.sendTemporaryPassword(request.getUserEmail());
        return ResponseEntity.ok("임시 비밀번호가 이메일로 발송되었습니다");
    }

    // 최초로그인 시 사용자 정보 저장
    @PostMapping("/init")
    public ResponseEntity<Void> initializeUserInfo(@AuthenticationPrincipal Integer userId,
                                                   @Valid @RequestBody RegisterUserInfoRequest request) {
        userService.initializeUserInfo(userId, request);
        return ResponseEntity.ok().build();
    }

    // 마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<UserNicknameAndUserSkinTypeResponse> getUserNicknameAndSkinTypes(@AuthenticationPrincipal Integer userId) {
        UserNicknameAndUserSkinTypeResponse response = myPageService.getUserNicknameAndSkinTypes(userId);
        return ResponseEntity.ok(response);
    }
}
