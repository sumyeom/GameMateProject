package com.example.gamemate.domain.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    FOLLOW("follow", "새 팔로워가 생겼습니다."),
    COMMENT("comment", "새로운 댓글이 달렸습니다."),
    MATCHING("matching", "매칭에 성공했습니다."),
    LIKE("like", "좋아요가 달렸습니다.");

    private final String name;
    private final String content;

    NotificationType(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
