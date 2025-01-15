package com.example.gamemate.domain.match.service;

import com.example.gamemate.domain.match.dto.*;
import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.entity.MatchUserInfo;
import com.example.gamemate.domain.match.enums.MatchStatus;
import com.example.gamemate.domain.match.repository.MatchDesiredInfoRepository;
import com.example.gamemate.domain.match.repository.MatchRepository;
import com.example.gamemate.domain.match.repository.MatchUserInfoRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchUserInfoRepository matchUserInfoRepository;
    private final MatchDesiredInfoRepository matchDesiredInfoRepository;

    // 매칭 요청 생성
    @Transactional
    public MatchResponseDto createMatch(MatchCreateRequestDto dto, User loginUser) {

        User receiver = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (receiver.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }

        if (matchRepository.existsBySenderAndReceiverAndStatus(loginUser, receiver, MatchStatus.PENDING)) {
            throw new ApiException(ErrorCode.IS_ALREADY_PENDING);
        }

        Match match = new Match(dto.getMessage(), loginUser, receiver);
        matchRepository.save(match);

        return MatchResponseDto.toDto(match);
    }

    // 매칭 수락/거절
    @Transactional
    public void updateMatch(Long id, MatchUpdateRequestDto dto, User loginUser) {

        Match findMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (findMatch.getStatus() != MatchStatus.PENDING) {
            throw new ApiException(ErrorCode.IS_ALREADY_PROCESSED);
        }

        if (!Objects.equals(loginUser.getId(), findMatch.getReceiver().getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        findMatch.updateStatus(dto.getStatus());
    }

    // 받은 매칭 전체 조회
    public List<MatchResponseDto> findAllReceivedMatch(User loginUser) {

        List<Match> matchList = matchRepository.findAllByReceiverId(loginUser.getId());

        return matchList
                .stream()
                .map(MatchResponseDto::toDto)
                .toList();
    }

    // 보낸 매칭 전체 조회
    public List<MatchResponseDto> findAllSentMatch(User loginUser) {

        List<Match> matchList = matchRepository.findAllBySenderId(loginUser.getId());

        return matchList
                .stream()
                .map(MatchResponseDto::toDto)
                .toList();
    }

    // 매치 삭제 (취소)
    @Transactional
    public void deleteMatch(Long id, User loginUser) {

        Match findMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (!Objects.equals(findMatch.getSender().getId(), loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        matchRepository.delete(findMatch);
    }

    // 내 정보 입력
    @Transactional
    public CreateMyInfoResponseDto createMyInfo(CreateMyInfoRequestDto dto, User loginUser) {

        MatchUserInfo matchUserInfo = new MatchUserInfo(
                dto.getGender(),
                dto.getLanes(),
                dto.getPurposes(),
                dto.getPlayTimeRanges(),
                dto.getGameRank(),
                dto.getSkillLevel(),
                dto.getMicUsage(),
                dto.getMessage(),
                loginUser
        );

        matchUserInfoRepository.save(matchUserInfo);

        return CreateMyInfoResponseDto.toDto(matchUserInfo);
    }
}
