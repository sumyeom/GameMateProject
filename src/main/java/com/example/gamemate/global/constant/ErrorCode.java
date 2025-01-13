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
    IS_WITHDRAW_USER(HttpStatus.BAD_REQUEST, "IS_WITHDRAW_USER", "탈퇴한 유저입니다."),

    /* 401 세션 없음 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
    NO_SESSION(HttpStatus.UNAUTHORIZED, "NO_SESSION","로그인이 필요합니다."),

    /* 404 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),
    USER_WITHDRAWN(HttpStatus.NOT_FOUND, "USER_WITHDRAWN", "탈퇴한 유저입니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND,"FOLLOW_NOT_FOUND", "팔로우를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND,"GAME_NOT_FOUND","게임을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"REVIEW_NOT_FOUND","리뷰를 찾을 수 없습니다."),

    /* 500 서버 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR","서버 오류 입니다."),;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
