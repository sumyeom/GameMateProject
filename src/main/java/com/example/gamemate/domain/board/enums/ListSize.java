package com.example.gamemate.domain.board.enums;

import lombok.Getter;

@Getter
public enum ListSize {
    LIST_SIZE(15);

    private final int size;
    ListSize(int size) {
        this.size = size;
    }

}
