package com.example.gamemate.domain.match.controller;

import com.example.gamemate.domain.match.dto.MatchFindResponseDto;
import com.example.gamemate.domain.match.dto.MatchUpdateRequestDto;
import com.example.gamemate.domain.match.service.MatchService;
import com.example.gamemate.domain.match.dto.MatchCreateRequestDto;
import com.example.gamemate.domain.match.dto.MatchCreateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 매칭 수락/거절하기
     * @param id 매칭 id
     * @param dto MatchUpdateRequestDto 수락/거절
     * @return 204 NO CONTENT
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateMatch(@PathVariable Long id, @RequestBody MatchUpdateRequestDto dto) {
        matchService.updateMatch(id, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 매칭 전체 조회
     * @return matchFindResponseDtoList
     */
    @GetMapping
    public ResponseEntity<List<MatchFindResponseDto>> findAllMatch() {
        List<MatchFindResponseDto> matchFindResponseDtoList = matchService.findAllMatch();
        return new ResponseEntity<>(matchFindResponseDtoList, HttpStatus.OK);
    }
}
