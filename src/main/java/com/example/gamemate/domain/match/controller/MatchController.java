package com.example.gamemate.domain.match.controller;

import com.example.gamemate.domain.match.dto.*;
import com.example.gamemate.domain.match.service.MatchService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
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
     * @return matchCreateResponseDto
     */
    @PostMapping
    public ResponseEntity<MatchResponseDto> createMatch(
            @RequestBody MatchCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchResponseDto matchResponseDto = matchService.createMatch(dto, customUserDetails.getUser());
        return new ResponseEntity<>(matchResponseDto, HttpStatus.CREATED);
    }

    /**
     * 매칭 수락/거절하기
     * @param id  매칭 id
     * @param dto MatchUpdateRequestDto 수락/거절
     * @return 204 NO CONTENT
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateMatch(
            @PathVariable Long id,
            @RequestBody MatchUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.updateMatch(id, dto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 받은 매칭 전체 조회
     * @return MatchResponseDtoList
     */
    @GetMapping("/received-matches")
    public ResponseEntity<List<MatchResponseDto>> findAllReceivedMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<MatchResponseDto> MatchResponseDtoList = matchService.findAllReceivedMatch(customUserDetails.getUser());
        return new ResponseEntity<>(MatchResponseDtoList, HttpStatus.OK);
    }

    /**
     * 보낸 매칭 전체 조회
     * @return MatchResponseDtoList
     */
    @GetMapping("/sent-matches")
    public ResponseEntity<List<MatchResponseDto>> findAllSentMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<MatchResponseDto> MatchResponseDtoList = matchService.findAllSentMatch(customUserDetails.getUser());
        return new ResponseEntity<>(MatchResponseDtoList, HttpStatus.OK);
    }

    /**
     * 매칭 삭제(취소)
     * @param id 매칭 id
     * @return NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.deleteMatch(id, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 매칭을 위해 내 정보 입력하기, 매칭 정보 입력시 매칭추천에서 검색됨
     * @param dto MatchInfoCreateRequestDto
     * @return matchInfoResponseDto
     */
    @PostMapping("/info")
    public ResponseEntity<MatchInfoResponseDto> createMyInfo(
            @RequestBody MatchInfoCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchInfoResponseDto matchInfoResponseDto = matchService.createMyInfo(dto, customUserDetails.getUser());
        return new ResponseEntity<>(matchInfoResponseDto, HttpStatus.CREATED);
    }

    /**
     * 매칭 내정보 조회하기
     * @return MatchInfoResponseDto
     */
    @GetMapping("/info")
    public ResponseEntity<MatchInfoResponseDto> findMyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchInfoResponseDto matchInfoResponseDto = matchService.findMyInfo(customUserDetails.getUser());
        return new ResponseEntity<>(matchInfoResponseDto, HttpStatus.OK);
    }

    /**
     * 매칭 내정보 수정하기
     * @param dto MatchInfoUpdateRequestDto
     * @return 204 NO_CONTENT
     */
    @PutMapping("/info")
    public ResponseEntity<Void> updateMyInfo(
            @Valid @RequestBody MatchInfoUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.updateMyInfo(dto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * 내 정보 삭제, 내 정보 삭제시 더이상 매칭에서 검색되지 않음
     * @return 204 NO_CONTENT
     */
    @DeleteMapping("/info")
    public ResponseEntity<Void> deleteMyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.deleteMyInfo(customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 매칭 추천 받기
     * @param dto MatchSearchConditionDto 매칭 조건 설정
     * @return recommendationList 매칭 로직을 통해 가장 점수가 높은 최대 5명 리스트
     */
    @PostMapping("/recommendations")
    public ResponseEntity<List<MatchInfoResponseDto>> findRecommendation(
            @Valid @RequestBody MatchSearchConditionDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<MatchInfoResponseDto> recommendationList = matchService.findRecommendation(dto, customUserDetails.getUser());
        return new ResponseEntity<>(recommendationList, HttpStatus.OK);
    }
}
