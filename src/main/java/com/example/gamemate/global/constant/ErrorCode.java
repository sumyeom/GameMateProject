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
    IS_ALREADY_FOLLOWED(HttpStatus.BAD_REQUEST, "IS_ALREADY_FOLLOWED", "이미 팔로우 한 회원입니다."),
    IS_WITHDRAWN_USER(HttpStatus.BAD_REQUEST, "IS_WITHDRAW_USER", "탈퇴한 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATE_USER", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"REVIEW_ALREADY_EXISTS","이미 리뷰를 작성한 회원입니다."),
    IS_ALREADY_PENDING(HttpStatus.BAD_REQUEST, "IS_ALREADY_PENDING", "이미 대기중인 요청이 있습니다."),
    IS_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "IS_ALREADY_PROCESSED", "이미 처리된 요청입니다."),

    /* 401 인증 오류 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
    NO_SESSION(HttpStatus.UNAUTHORIZED, "NO_SESSION","로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),

    /* 403 권한 없음 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다."),

    /* 404 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND,"FOLLOW_NOT_FOUND", "팔로우를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND,"GAME_NOT_FOUND","게임을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"REVIEW_NOT_FOUND","리뷰를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH_NOT_FOUND", "매칭을 찾을 수 없습니다."),
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND,"RECOMMENDATION_NOT_FOUND","추천 게임을 찾을 수 없습니다."),

    /* 500 서버 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR","서버 오류 입니다."),;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
