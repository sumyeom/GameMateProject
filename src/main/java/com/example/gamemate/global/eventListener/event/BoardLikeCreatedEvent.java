package com.example.gamemate.global.eventListener.event;

import com.example.gamemate.domain.like.entity.BoardLike;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BoardLikeCreatedEvent extends ApplicationEvent {
    private final BoardLike boardLike;

    public BoardLikeCreatedEvent(Object source, BoardLike boardLike) {
        super(source);
        this.boardLike = boardLike;
    }
}
