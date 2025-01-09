package com.example.gamemate.domain.match.controller;

import com.example.gamemate.domain.match.service.MatchService;
import com.example.gamemate.domain.match.dto.MatchCreateRequestDto;
import com.example.gamemate.domain.match.dto.MatchCreateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {
    private final MatchService matchService;

    /**
     * 매칭 요청 생성
     * @param dto MatchCreateRequestDto 상대 유저 id, 상대방에게 전할 메세지
     * @return message = "매칭이 요청되었습니다."
     */
    @PostMapping
    public ResponseEntity<MatchCreateResponseDto> createMatch(@RequestBody MatchCreateRequestDto dto) {
        MatchCreateResponseDto matchCreateResponseDto = matchService.createMatch(dto);
        return new ResponseEntity<>(matchCreateResponseDto, HttpStatus.CREATED);
    }
}
