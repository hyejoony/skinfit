package com.ssafy12.moinsoop.skinfit.domain.user.entity;

import com.ssafy12.moinsoop.skinfit.domain.skintype.entity.UserSkinType;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.converter.GenderConverter;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.converter.ProviderTypeConverter;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.converter.RoleTypeConverter;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.enums.Gender;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.enums.ProviderType;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 100)
    private String userPassword;

    @Column(name = "is_registered", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isRegistered;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Convert(converter = GenderConverter.class)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_year")
    private Year birthYear;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Convert(converter = RoleTypeConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    @Convert(converter = ProviderTypeConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserSkinType> userSkinTypes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(String userEmail, String userPassword, String nickname,
                Gender gender, Year birthYear, ProviderType providerType,
                RoleType roleType, boolean isRegistered) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.gender = gender;
        this.birthYear = birthYear;
        this.isRegistered = isRegistered;
        this.roleType = roleType != null ? roleType : RoleType.USER;
        this.providerType = providerType != null ? providerType : ProviderType.LOCAL;
    }

    // 비밀번호 업데이트 메서드 추가
    public void updatePassword(String encodedPassword) {
        this.userPassword = encodedPassword;
    }

    // 회원 정보 최초 입력에 필요한 메서드
    public void updateInitialInfo(String nickname, Gender gender, Year birthYear) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthYear = birthYear;
    }

    // 회원 정보 최초 입력 시 false -> true 로 변환해줘야 한다.
    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
    }

    public void updateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.nickname = nickname;
    }
}