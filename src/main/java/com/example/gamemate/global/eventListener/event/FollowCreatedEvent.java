package com.example.gamemate.global.eventListener.event;

import com.example.gamemate.domain.follow.entity.Follow;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FollowCreatedEvent extends ApplicationEvent {
    private final Follow follow;

    public FollowCreatedEvent(Object source, Follow follow) {
        super(source);
        this.follow = follow;
    }
}
