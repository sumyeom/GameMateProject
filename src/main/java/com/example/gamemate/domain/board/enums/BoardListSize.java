package com.example.gamemate.domain.board.enums;

import lombok.Getter;

@Getter
public enum BoardListSize {
    LIST_SIZE(15);

    private final int size;
    BoardListSize(int size) {
        this.size = size;
    }

}
