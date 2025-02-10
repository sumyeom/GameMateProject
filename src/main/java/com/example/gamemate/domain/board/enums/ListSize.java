package com.example.gamemate.domain.board.enums;

import lombok.Getter;

@Getter
public enum ListSize {
    BOARD_LIST_SIZE(15),
    COMMENT_LIST_SIZE(25),;


    private final int size;
    ListSize(int size) {
        this.size = size;
    }

}
