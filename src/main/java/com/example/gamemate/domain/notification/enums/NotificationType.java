package com.example.gamemate.domain.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    NEW_FOLLOWER("follow", "새 팔로워가 생겼습니다."),
    NEW_COMMENT("comment", "새로운 댓글이 달렸습니다."),
    NEW_MATCH("new_match", "새로운 매칭 요청이 왔습니다."),
    MATCH_REJECTED("match_rejected", "보낸 매칭이 거절되었습니다."),
    MATCH_ACCEPTED("match_accepted", "보낸 매칭이 수락되었습니다."),
    NEW_LIKE("like", "새 좋아요가 달렸습니다.");

    private final String name;
    private final String content;

    NotificationType(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
