package com.example.gamemate.domain.match.service;

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
}
