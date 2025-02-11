package com.ssafy12.moinsoop.skinfit.domain.user.service;

import com.ssafy12.moinsoop.skinfit.domain.cosmetic.entity.repository.CosmeticRepository;
import com.ssafy12.moinsoop.skinfit.domain.experience.entity.CosmeticExperience;
import com.ssafy12.moinsoop.skinfit.domain.experience.entity.repository.CosmeticExperienceRepository;
import com.ssafy12.moinsoop.skinfit.domain.review.entity.ReviewLike;
import com.ssafy12.moinsoop.skinfit.domain.review.entity.repository.ReviewLikeRepository;
import com.ssafy12.moinsoop.skinfit.domain.review.entity.repository.ReviewRepository;
import com.ssafy12.moinsoop.skinfit.domain.skintype.entity.SkinType;
import com.ssafy12.moinsoop.skinfit.domain.skintype.entity.UserSkinType;
import com.ssafy12.moinsoop.skinfit.domain.skintype.entity.repository.UserSkinTypeRepository;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.request.CosmeticUpdateRequest;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.response.MyCosmeticsResponse;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.response.MyReviewResponse;
import com.ssafy12.moinsoop.skinfit.domain.user.dto.response.UserNicknameAndUserSkinTypeResponse;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.User;
import com.ssafy12.moinsoop.skinfit.domain.user.entity.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final UserSkinTypeRepository userSkinTypeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CosmeticExperienceRepository cosmeticExperienceRepository;
    private final CosmeticRepository cosmeticRepository;

    @Transactional(readOnly = true)
    public UserNicknameAndUserSkinTypeResponse getUserNicknameAndSkinTypes(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        // 사용자의 스킨 타입들
        List<String> userSkinTypes = userSkinTypeRepository.findAllByUser_UserId(userId)
                .stream()
                .map(UserSkinType::getSkinType)
                .map(SkinType::getTypeName)
                .toList();

        // 응답 객체 반환
        return new UserNicknameAndUserSkinTypeResponse(user.getNickname(), userSkinTypes);
    }

    // top3 .. 다시 구현해야 함.

    // 내가 등록한 화장품, 맞는 것과 안맞는 것으로 구분하여 응답
    @Transactional(readOnly = true)
    public MyCosmeticsResponse getAllMyCosmetics(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        List<CosmeticExperience> experiences = cosmeticExperienceRepository.findByUserId(userId);

        List<MyCosmeticsResponse.CosmeticExperienceDto> suitableList = experiences.stream()
                .filter(CosmeticExperience::isSuitable)
                .map(this::convertToDto)
                .toList();

        List<MyCosmeticsResponse.CosmeticExperienceDto> unsuitalbeList = experiences.stream()
                .filter(exp -> !exp.isSuitable())
                .map(this::convertToDto)
                .toList();

        return MyCosmeticsResponse.builder()
                .suitableCosmetics(suitableList)
                .unsuitableCosmetics(unsuitalbeList)
                .build();
    }

    @Transactional(readOnly = true)
    public MyReviewResponse getAllMyReviews(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        List<MyReviewResponse.ReviewDto> myReviews = reviewRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MyReviewResponse.ReviewDto::from)
                .toList();

        List<MyReviewResponse.ReviewDto> likedReviews = reviewLikeRepository.findByUser_UserId(userId)
                .stream()
                .map(ReviewLike::getReview)
                .map(MyReviewResponse.ReviewDto::from)
                .toList();

        return MyReviewResponse.builder()
                .myReviews(myReviews)
                .likedReviews(likedReviews)
                .build();
    }

    /**
     * 나와 맞는 화장품을 수정하는 폼으로 이동하는 메서드
     * GET 요청이기 떄문에 사용자가 등록한 맞는 화장품을 응답해주어야 한다.
     */
    @Transactional(readOnly = true)
    public List<MyCosmeticsResponse.CosmeticExperienceDto> getAllSuitableCosmetics(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다"));

        List<CosmeticExperience> experiences = cosmeticExperienceRepository.findByUserId(userId);

        List<MyCosmeticsResponse.CosmeticExperienceDto> suitableList = experiences.stream()
                .filter(CosmeticExperience::isSuitable)
                .map(this::convertToDto)
                .toList();

        return suitableList;
    }

    // 나에게 맞는 화장품 수정 메서드
    @Transactional
    public void updateSuitableCosmetics(Integer userId, List<CosmeticUpdateRequest> requests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다"));

        // 1. 기존 잘 맞는 화장품 목록 조회
        List<CosmeticExperience> existingExperiences = cosmeticExperienceRepository.findByUser_UserIdAndIsSuitableTrue(userId);
        // 2. 요청된 화장품 ID 목록 추출
        List<Integer> newCosmeticIds = requests.stream()
                .map(CosmeticUpdateRequest::getCosmeticId)
                .toList();
        // 3. 삭제할 거 찾기 (새 목록에 없는 기존 경험들)
        List<CosmeticExperience> experiencesToDelete = existingExperiences.stream()
                .filter(exp -> !newCosmeticIds.contains(exp.getCosmetic().getCosmeticId()))
                .toList();
        // 4. 새로 추가할 경험 생성
        List<CosmeticExperience> experiencesToAdd = CosmeticExperience.updateCosmeticExperiences(
                user,
                existingExperiences,
                newCosmeticIds,
                true,
                cosmeticRepository
        );

        // 5. 삭제
        cosmeticExperienceRepository.deleteAll(experiencesToDelete);
        // 6. 등록
        cosmeticExperienceRepository.saveAll(experiencesToAdd);
    }

    private MyCosmeticsResponse.CosmeticExperienceDto convertToDto(CosmeticExperience experience) {
        return MyCosmeticsResponse.CosmeticExperienceDto.builder()
                .cosmeticId(experience.getCosmetic().getCosmeticId())
                .cosmeticBrand(experience.getCosmetic().getCosmeticBrand())
                .cosmeticName(experience.getCosmetic().getCosmeticName())
                .cosmeticVolume(experience.getCosmetic().getCosmeticVolume())
                .imageUrl(experience.getCosmetic().getImageUrl())
                .build();
    }
}
