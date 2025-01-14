package com.example.gamemate.domain.match.controller;

import com.example.gamemate.domain.match.dto.MatchResponseDto;
import com.example.gamemate.domain.match.dto.MatchUpdateRequestDto;
import com.example.gamemate.domain.match.service.MatchService;
import com.example.gamemate.domain.match.dto.MatchCreateRequestDto;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<MatchResponseDto> createMatch(
            @RequestBody MatchCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchResponseDto matchCreateResponseDto = matchService.createMatch(dto, customUserDetails.getUser());
        return new ResponseEntity<>(matchCreateResponseDto, HttpStatus.CREATED);
    }

    /**
     * 매칭 수락/거절하기
     * @param id 매칭 id
     * @param dto MatchUpdateRequestDto 수락/거절
     * @return 204 NO CONTENT
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateMatch(
            @PathVariable Long id,
            @RequestBody MatchUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        matchService.updateMatch(id, dto, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 받은 매칭 전체 조회
     * @return matchFindResponseDtoList
     */
    @GetMapping("/received-match")
    public ResponseEntity<List<MatchResponseDto>> findAllReceivedMatch(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        List<MatchResponseDto> matchResponseDtoList = matchService.findAllReceivedMatch(userDetails.getUser());
        return new ResponseEntity<>(matchResponseDtoList, HttpStatus.OK);
    }

    /**
     * 보낸 매칭 전체 조회
     * @return matchFindResponseDtoList
     */
    @GetMapping("/sent-match")
    public ResponseEntity<List<MatchResponseDto>> findAllSentMatch(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        List<MatchResponseDto> matchResponseDtoList = matchService.findAllSentMatch(userDetails.getUser());
        return new ResponseEntity<>(matchResponseDtoList, HttpStatus.OK);
    }

    /**
     * 매칭 삭제(취소)
     * @param id 매칭 id
     * @return NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        matchService.deleteMatch(id, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
