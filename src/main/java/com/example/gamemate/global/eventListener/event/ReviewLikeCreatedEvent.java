package com.example.gamemate.global.eventListener.event;

import com.example.gamemate.domain.like.entity.ReviewLike;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReviewLikeCreatedEvent extends ApplicationEvent {
    private final ReviewLike reviewLike;

    public ReviewLikeCreatedEvent(Object source, ReviewLike reviewLike) {
        super(source);
        this.reviewLike = reviewLike;
    }
}
