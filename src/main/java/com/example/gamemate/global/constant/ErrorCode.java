package com.example.gamemate.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 잘못된 입력값 */
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "잘못된 요청입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "잘못된 요청입니다."),
    IS_ALREADY_FOLLOWED(HttpStatus.BAD_REQUEST, "IS_ALREADY_FOLLOWED", "이미 팔로우 한 유저입니다."),

    /* 401 세션 없음 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
    NO_SESSION(HttpStatus.UNAUTHORIZED, "NO_SESSION","로그인이 필요합니다."),


    /* 500 서버 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR","서버 오류 입니다."),;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
