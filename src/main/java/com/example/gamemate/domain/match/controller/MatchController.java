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

/**
 * 매칭 기능을 처리하는 컨트롤러 클래스입니다.
 * 사용자 간의 매칭 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {
    private final MatchService matchService;

    /**
     * 사용자 간의 매칭 요청을 생성합니다.
     *
     * @param dto 매칭을 원하는 상대방 ID, 상대방에게 보낼 메세지를 포함합니다.
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 매칭 요청 처리 결과를 담은 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<MatchResponseDto> createMatch(
            @Valid @RequestBody MatchCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchResponseDto matchResponseDto = matchService.createMatch(dto, customUserDetails.getUser());
        return new ResponseEntity<>(matchResponseDto, HttpStatus.CREATED);
    }

    /**
     * 받은 매칭 요청의 수락/거절을 처리합니다.
     *
     * @param id 수락/거절할 매칭 요청 ID
     * @param dto status (ACCEPTED 수락 / REJECTED 거절)
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 204 NO_CONTENT 성공했지만 반환값이 없음
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateMatch(
            @PathVariable Long id,
            @Valid @RequestBody MatchUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.updateMatch(id, dto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * 사용자가 받은 매칭 요청을 조회합니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 사용자의 받은 매칭 요청 목록을 담은 ResponseEntity
     */
    @GetMapping("/received-matches")
    public ResponseEntity<List<MatchResponseDto>> findAllReceivedMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<MatchResponseDto> matchResponseDtoList = matchService.findAllReceivedMatch(customUserDetails.getUser());
        return new ResponseEntity<>(matchResponseDtoList, HttpStatus.OK);
    }

    /**
     * 사용자가 보낸 매칭 요청을 조회합니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 사용자가 보낸 매칭 요청 목록을 담은 ResponseEntity
     */
    @GetMapping("/sent-matches")
    public ResponseEntity<List<MatchResponseDto>> findAllSentMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        List<MatchResponseDto> matchResponseDtoList = matchService.findAllSentMatch(customUserDetails.getUser());
        return new ResponseEntity<>(matchResponseDtoList, HttpStatus.OK);
    }

    /**
     * 사용자가 보낸 매칭 요청을 취소합니다.
     *
     * @param id 취소할 매칭 요청 ID
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 204 NO_CONTENT 성공했지만 반환값 없음
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
     * 매칭을 위한 정보를 입력합니다.
     *
     * @param dto 매칭을 위해 자신의 정보를 입력합니다.
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 사용자의 정보가 처리된 ResponseEntity
     */
    @PostMapping("/my-info")
    public ResponseEntity<MatchInfoResponseDto> createMyInfo(
            @Valid @RequestBody MatchInfoCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchInfoResponseDto matchInfoResponseDto = matchService.createMyInfo(dto, customUserDetails.getUser());
        return new ResponseEntity<>(matchInfoResponseDto, HttpStatus.CREATED);
    }

    /**
     * 매칭을 위해 입력한 내 정보를 확인합니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 내 정보를 담은 ResponseEntity
     */
    @GetMapping("/my-info")
    public ResponseEntity<MatchInfoResponseDto> findMyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchInfoResponseDto matchInfoResponseDto = matchService.findMyInfo(customUserDetails.getUser());
        return new ResponseEntity<>(matchInfoResponseDto, HttpStatus.OK);
    }

    /**
     * 매칭 상대방의 입력한 정보를 확인합니다.
     *
     * @param id 확인할 매칭 요청 ID
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 매칭 요청 ID의 상대방이 입력한 정보를 담은 ResponseEntity
     */
    @GetMapping("/{id}/opponent-info")
    public ResponseEntity<MatchInfoResponseDto> findOpponentInfo(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        MatchInfoResponseDto matchInfoResponseDto = matchService.findOpponentInfo(id, customUserDetails.getUser());
        return new ResponseEntity<>(matchInfoResponseDto, HttpStatus.OK);
    }

    /**
     * 입력한 내 정보를 수정합니다.
     *
     * @param dto 수정할 정보를 입력합니다.
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 204 NO_CONTENT 성공했지만 반환값 없음
     */
    @PutMapping("/my-info")
    public ResponseEntity<Void> updateMyInfo(
            @Valid @RequestBody MatchInfoUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.updateMyInfo(dto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 내 정보 삭제, 내 정보 삭제시 더이상 매칭에서 검색되지 않습니다.
     *
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 204 NO_CONTENT 성공했지만 반환값 없음
     */
    @DeleteMapping("/my-info")
    public ResponseEntity<Void> deleteMyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        matchService.deleteMyInfo(customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 매칭 추천 받기
     *
     * @param dto 원하는 매칭 조건을 설정합니다.
     * @param customUserDetails 현재 인증된 사용자 정보
     * @return 원하는 매칭 조건을 토대로 매칭 로직을 통해 가장 점수가 높은 5명을 추천해줍니다.
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
