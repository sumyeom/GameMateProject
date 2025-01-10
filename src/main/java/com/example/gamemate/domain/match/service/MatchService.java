package com.example.gamemate.domain.match.service;

import com.example.gamemate.domain.match.dto.MatchFindResponseDto;
import com.example.gamemate.domain.match.dto.MatchUpdateRequestDto;
import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.enums.MatchStatus;
import com.example.gamemate.domain.match.dto.MatchCreateRequestDto;
import com.example.gamemate.domain.match.dto.MatchCreateResponseDto;
import com.example.gamemate.domain.match.repository.MatchRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    // 매칭 요청 생성
    // todo : 현재 로그인이 구현되어 있지 않아, 로그인 유저를 1번 유저로 설정
    @Transactional
    public MatchCreateResponseDto createMatch(MatchCreateRequestDto dto) {
        User loginUser = userRepository.findById(1L).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(dto.getUserId()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (receiver.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAW_USER);
        }

        if (matchRepository.existsBySenderAndReceiverAndStatus(loginUser, receiver, MatchStatus.PENDING)) {
            throw new ApiException(ErrorCode.IS_ALREADY_PENDING);
        }

        Match match = new Match(dto.getMessage(), loginUser, receiver);
        matchRepository.save(match);

        return new MatchCreateResponseDto("매칭이 요청되었습니다.");
    }

    // 매칭 수락/거절
    // todo : 현재 로그인이 구현되어 있지 않아, receiver 를 1번 유저로 설정. 로그인 구현시 수정필요
    @Transactional
    public void updateMatch(Long id, MatchUpdateRequestDto dto) {
        Match findMatch = matchRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (findMatch.getStatus() != MatchStatus.PENDING) {
            throw new ApiException(ErrorCode.IS_ALREADY_PROCESSED);
        }

        User loginUser = userRepository.findById(1L).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (loginUser != findMatch.getReceiver()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        findMatch.updateStatus(dto.getStatus());
    }

    // 매칭 전체 조회
    // todo : 현재 로그인이 구현 되어 있지 않아, 1번 유저의 목록을 불러오도록 설정. 로그인 구현시 수정 필요
    public List<MatchFindResponseDto> findAllMatch() {
        List<Match> matchList = matchRepository.findAllByReceiverId(1L);

        return matchList.stream().map(MatchFindResponseDto::toDto).toList();
    }
}
