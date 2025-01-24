package com.example.gamemate.domain.match.service;

import com.example.gamemate.domain.match.dto.*;
import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.entity.MatchUserInfo;
import com.example.gamemate.domain.match.enums.GameRank;
import com.example.gamemate.domain.match.enums.MatchStatus;
import com.example.gamemate.domain.match.enums.Priority;
import com.example.gamemate.domain.match.repository.MatchRepository;
import com.example.gamemate.domain.match.repository.MatchUserInfoRepository;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.gamemate.domain.match.enums.Priority.*;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchUserInfoRepository matchUserInfoRepository;
    private final NotificationService notificationService;

    // 매칭 요청 생성
    @Transactional
    public MatchResponseDto createMatch(MatchCreateRequestDto dto, User loginUser) {

        User receiver = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (!matchUserInfoRepository.existsByUser(receiver)) {
            throw new ApiException(ErrorCode.MATCH_USER_INFO_NOT_FOUND);
        } // 받는 사람의 매칭 유저 정보가 없을때 예외처리

        if (!matchUserInfoRepository.existsByUser(loginUser)) {
            throw new ApiException(ErrorCode.MATCH_USER_INFO_NOT_WRITTEN);
        } // 로그인 한 유저의 매칭 유저 정보가 없을때 예외처리

        if (receiver.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        } // 받는 사람의 유저 상태가 탈퇴 상태일때 예외처리

        if (matchRepository.existsBySenderAndReceiverAndStatus(loginUser, receiver, MatchStatus.PENDING)) {
            throw new ApiException(ErrorCode.IS_ALREADY_PENDING);
        } // 이미 보낸 요청이 있을때 예외처리

        Match match = new Match(dto.getMessage(), loginUser, receiver);
        Match savedMatch = matchRepository.save(match);
        notificationService.sendNotification(receiver, NotificationType.NEW_MATCH, "/matches/" + savedMatch.getId());

        return MatchResponseDto.toDto(match);
    }

    // 매칭 수락/거절
    @Transactional
    public void updateMatch(Long id, MatchUpdateRequestDto dto, User loginUser) {

        Match findMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (findMatch.getStatus() != MatchStatus.PENDING) {
            throw new ApiException(ErrorCode.IS_ALREADY_PROCESSED);
        } // 매칭의 상태가 보류중이 아닐때 예외처리

        if (!Objects.equals(loginUser.getId(), findMatch.getReceiver().getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        } // 로그인한 유저가 매칭의 받는 사람이 아닐때 예외처리

        if (dto.getStatus() == MatchStatus.ACCEPTED) {
            notificationService.sendNotification(findMatch.getSender(), NotificationType.MATCH_ACCEPTED, "/matches/" + findMatch.getId());
        } // 매칭 보낸 사람에게 매칭이 수락되었다는 알림 전송

        if (dto.getStatus() == MatchStatus.REJECTED) {
            notificationService.sendNotification(findMatch.getSender(), NotificationType.MATCH_REJECTED, "/matches/" + findMatch.getId());
        } // 매칭 보낸 사람에게 매칭이 거절되었다는 알림 전송

        findMatch.updateStatus(dto.getStatus());
    }

    // 받은 매칭 전체 조회
    public List<MatchResponseDto> findAllReceivedMatch(User loginUser) {

        List<Match> matchList = matchRepository.findAllByReceiverId(loginUser.getId());

        return matchList.stream()
                .map(MatchResponseDto::toDto)
                .toList();
    }

    // 보낸 매칭 전체 조회
    public List<MatchResponseDto> findAllSentMatch(User loginUser) {

        List<Match> matchList = matchRepository.findAllBySenderId(loginUser.getId());

        return matchList.stream()
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
        } // 로그인한 유저가 매칭의 보낸사람이 아닐때 예외처리

        matchRepository.delete(findMatch);
    }

    // 내 정보 입력, 정보 입력시 매칭 추천에서 검색됨
    @Transactional
    public MatchInfoResponseDto createMyInfo(MatchInfoCreateRequestDto dto, User loginUser) {

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

        return MatchInfoResponseDto.toDto(matchUserInfo);
    }

    // 내 정보 조회
    public MatchInfoResponseDto findMyInfo(User loginUser) {
        MatchUserInfo matchUserInfo = matchUserInfoRepository.findByUser(loginUser)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_USER_INFO_NOT_FOUND));

        return MatchInfoResponseDto.toDto(matchUserInfo);
    }

    // 상대방 정보 조회
    public MatchInfoResponseDto findOpponentInfo(Long id, User loginUser) {
        Match findMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (!Objects.equals(findMatch.getReceiver().getId(), loginUser.getId())
                && !Objects.equals(findMatch.getSender().getId(), loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        } // 매칭의 상대방을 검색할때, 로그인한 유저가 검색할 매칭과 연관이 없을때 예외처리

        return (Objects.equals(findMatch.getReceiver().getId(), loginUser.getId()))
                ? getMatchInfoResponseDto(findMatch.getSender())
                : getMatchInfoResponseDto(findMatch.getReceiver());
    }

    // 내 정보 수정
    @Transactional
    public void updateMyInfo(MatchInfoUpdateRequestDto dto, User loginUser) {
        MatchUserInfo matchUserInfo = matchUserInfoRepository.findByUser(loginUser)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_USER_INFO_NOT_FOUND));

        matchUserInfo.updateMatchUserInfo(
                dto.getGender(),
                dto.getLanes(),
                dto.getPurposes(),
                dto.getPlayTimeRanges(),
                dto.getGameRank(),
                dto.getSkillLevel(),
                dto.getMicUsage(),
                dto.getMessage()
        );
    }

    // 내 정보 삭제, 내정보 삭제시 매칭 추천에서 더이상 검색되지 않음
    @Transactional
    public void deleteMyInfo(User loginUser) {
        MatchUserInfo matchUserInfo = matchUserInfoRepository.findByUser(loginUser)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_USER_INFO_NOT_FOUND));

        matchUserInfoRepository.delete(matchUserInfo);
    }

    // 매칭 로직
    public List<MatchInfoResponseDto> findRecommendation(MatchSearchConditionDto dto, User loginUser) {
        // 1. 성별과 플레이 시간대를 기준으로 필터링된 사용자 정보 조회
        List<MatchUserInfo> filteredUsers = matchUserInfoRepository.findByGenderAndPlayTimeRanges(
                dto.getGender(),
                dto.getPlayTimeRanges(),
                loginUser.getId()
        );

        // 2. 매칭 점수 계산 및 저장
        for (MatchUserInfo matchUserInfo : filteredUsers) {
            int score = calculateMatchScore(dto, matchUserInfo);
            matchUserInfo.updateMatchScore(score);
        }

        // 3. 매칭 점수 내림차순으로 정렬
        filteredUsers.sort((u1, u2) -> Integer.compare(u2.getMatchScore(), u1.getMatchScore()));


        // 4. 동점자 처리 (동점자끼리 랜덤 섞기)
        List<MatchUserInfo> resultList = handleTies(filteredUsers);

        // 5. 상위 5명 추출 및 DTO 변환
        return resultList.stream()
                .limit(5)
                .map(MatchInfoResponseDto::toDto)
                .collect(Collectors.toList());
    }


    // 점수 계산 로직
    private int calculateMatchScore(MatchSearchConditionDto condition, MatchUserInfo userInfo) {
        int score = 0;
        int normalScorePerMatch = 5; // 매칭되는 항목당 점수
        int priorityWeight = 2; // 우선순위 가중치

        Priority priority = condition.getPriority();

        // 우선순위 항목 점수 계산 및 가중치 적용
        if (priority != null) {
            switch (priority) {
                case LANES:
                    int matchedLanes = (int) condition.getLanes().stream()
                            .filter(userInfo.getLanes()::contains)
                            .count();
                    score += matchedLanes * normalScorePerMatch * priorityWeight;
                    break;
                case PURPOSES:
                    int matchedPurposes = (int) condition.getPurposes().stream()
                            .filter(userInfo.getPurposes()::contains)
                            .count();
                    score += matchedPurposes * normalScorePerMatch * priorityWeight;
                    break;
                case PLAY_TIME_RANGES:
                    int matchedPlayTimeRanges = (int) condition.getPlayTimeRanges().stream()
                            .filter(userInfo.getPlayTimeRanges()::contains)
                            .count();
                    score += matchedPlayTimeRanges * normalScorePerMatch * priorityWeight;
                    break;
                case GAME_RANK:
                    if (condition.getGameRank().equals(userInfo.getGameRank())) {
                        score += normalScorePerMatch * priorityWeight * 2;
                    } else if (isRankSimilar(condition.getGameRank(), userInfo.getGameRank())) {
                        score += normalScorePerMatch * priorityWeight;
                    }
                    break;
                case SKILL_LEVEL:
                    int skillLevelDifference = Math.abs(condition.getSkillLevel() - userInfo.getSkillLevel());
                    score += (normalScorePerMatch - skillLevelDifference) * priorityWeight;
                    break;
                case MIC_USAGE:
                    if (condition.getMicUsage().equals(userInfo.getMicUsage())) {
                        score += normalScorePerMatch * priorityWeight * 2;
                    }
                    break;
            }
        }

        // 우선순위가 아닌 조건의 점수 계산 방식
        if (priority == null || !priority.equals(LANES)) {
            int matchedLanes = (int) condition.getLanes().stream()
                    .filter(userInfo.getLanes()::contains)
                    .count();
            score += matchedLanes * normalScorePerMatch;
        }

        if (priority == null || !priority.equals(PURPOSES)) {
            int matchedPurposes = (int) condition.getPurposes().stream()
                    .filter(userInfo.getPurposes()::contains)
                    .count();
            score += matchedPurposes * normalScorePerMatch;
        }

        if (priority == null || !priority.equals(PLAY_TIME_RANGES)) {
            int matchedPlayTimeRanges = (int) condition.getPlayTimeRanges().stream()
                    .filter(userInfo.getPlayTimeRanges()::contains)
                    .count();
            score += matchedPlayTimeRanges * normalScorePerMatch;
        }

        if (priority == null || !priority.equals(GAME_RANK)) {
            if (condition.getGameRank().equals(userInfo.getGameRank())) {
                score += normalScorePerMatch * 2;
            } else if (isRankSimilar(condition.getGameRank(), userInfo.getGameRank())) {
                score += normalScorePerMatch;
            }
        }

        if (priority == null || !priority.equals(SKILL_LEVEL)) {
            int skillLevelDifference = Math.abs(condition.getSkillLevel() - userInfo.getSkillLevel());
            score += (normalScorePerMatch - skillLevelDifference);
        }

        if (priority == null || !priority.equals(MIC_USAGE)) {
            if (condition.getMicUsage().equals(userInfo.getMicUsage())) {
                score += normalScorePerMatch * 2;
            }
        }

        return score;
    }


    // 랭크가 비슷한지 판단하는 로직 추가
    private boolean isRankSimilar(GameRank conditionRank, GameRank userRank) {
        if (conditionRank == GameRank.DONT_MIND) {
            return true; // "상관없음"은 모든 랭크와 유사하다고 판단
        }

        int conditionRankIndex = conditionRank.ordinal();
        int userRankIndex = userRank.ordinal();
        return Math.abs(conditionRankIndex - userRankIndex) <= 1; // 랭크 차이가 1 이하면 유사하다고 판단
    }


    // 동점자는 랜덤으로 섞어서 출력
    private List<MatchUserInfo> handleTies(List<MatchUserInfo> sortedUsers) {
        if (sortedUsers.isEmpty()) {
            return sortedUsers;
        }
        List<MatchUserInfo> resultList = new ArrayList<>();
        List<MatchUserInfo> tieGroup = new ArrayList<>(); // 동점자 그룹 임시 저장

        tieGroup.add(sortedUsers.get(0));

        for (int i = 1; i < sortedUsers.size(); i++) {
            MatchUserInfo currentUser = sortedUsers.get(i);
            MatchUserInfo previousUser = sortedUsers.get(i - 1);

            if (currentUser.getMatchScore() == previousUser.getMatchScore()) {
                tieGroup.add(currentUser);
            } else {
                Collections.shuffle(tieGroup); // 동점자 그룹 섞기
                resultList.addAll(tieGroup);
                tieGroup.clear(); // 다음 그룹을 위해 비우기
                tieGroup.add(currentUser); //새로운 그룹 시작
            }
        }

        Collections.shuffle(tieGroup);
        resultList.addAll(tieGroup); //마지막 그룹 추가

        return resultList;
    }

    // 매칭 상대방 정보 찾아서 dto 로 변환
    private MatchInfoResponseDto getMatchInfoResponseDto(User user) {
        MatchUserInfo matchUserInfo = matchUserInfoRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return MatchInfoResponseDto.toDto(matchUserInfo);
    }
}

