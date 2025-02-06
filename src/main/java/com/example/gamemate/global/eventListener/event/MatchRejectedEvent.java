package com.example.gamemate.global.eventListener.event;

import com.example.gamemate.domain.match.entity.Match;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MatchRejectedEvent extends ApplicationEvent {
    private final Match match;

    public MatchRejectedEvent(Object source, Match match) {
        super(source);
        this.match = match;
    }
}