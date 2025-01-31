package com.example.gamemate.global.eventListener.event;

import com.example.gamemate.domain.match.entity.Match;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MatchAcceptedEvent extends ApplicationEvent {
    private final Match match;

    public MatchAcceptedEvent(Object source, Match match) {
        super(source);
        this.match = match;
    }
}