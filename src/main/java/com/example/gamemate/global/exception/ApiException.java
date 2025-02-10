package com.example.gamemate.global.exception;

import com.example.gamemate.global.constant.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
}
