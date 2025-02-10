package com.example.gamemate.domain.board.enums;

import com.example.gamemate.global.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.example.gamemate.global.constant.ErrorCode.INVALID_INPUT;

@Getter
public enum BoardCategory {
    FREE("자유_게시판"),
    INFO("정보_게시판"),
    RECRUIT("모집_게시판");

    private final String name;
    BoardCategory(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName(){
        return name;
    }

    @JsonCreator
    public static BoardCategory fromName(String name) {
        for(BoardCategory category : BoardCategory.values()){
            if(category.name().equalsIgnoreCase(name) || category.getName().equals(name)){
                return category;
            }
        }
        throw new ApiException(INVALID_INPUT);
    }

}
