package com.ssafy12.moinsoop.skinfit.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {

    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    COSMETIC_NOT_FOUND("해당 화장품을 찾을 수 없습니다."),
    REVIEW_CREATION_FAILED("리뷰 작성에 실패하였습니다."),
    REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다.");

    private final String message;
}