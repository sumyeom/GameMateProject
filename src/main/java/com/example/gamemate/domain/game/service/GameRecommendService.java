package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.*;
import com.example.gamemate.domain.game.entity.GameRecommendHistory;
import com.example.gamemate.domain.game.entity.UserGamePreference;
import com.example.gamemate.domain.game.repository.GameRecommendHistoryRepository;
import com.example.gamemate.domain.game.repository.UserGamePreferenceRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRecommendService {
    private final UserGamePreferenceRepository userGamePreferenceRepository;
    private final UserRepository userRepository;
    private final GameRecommendHistoryRepository gameRecommendHistoryRepository;
    private final GeminiService geminiService;

    @Transactional
    public UserGamePreferenceResponseDto createUserGamePreference(UserGamePreferenceRequestDto requestDto, User loginUser) {

        User user = userRepository.findById(loginUser.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        UserGamePreference userGamePreference = new UserGamePreference(
                user,
                requestDto.getPreferredGenres(),
                requestDto.getPlayStyle(),
                requestDto.getPlayTime(),
                requestDto.getDifficulty(),
                requestDto.getPlatform(),
                requestDto.getExtraRequest()
        );

        UserGamePreference saveData = userGamePreferenceRepository.save(userGamePreference);

        // 저장된 선호도를 기반으로 Gemini API 호출
        String prompt = String.format(
                "나에게 맞는 게임 3개 추천해줘 선호하는 장르는 %s이고 플레이 스타일은 %s 정도고 플레이 타임은 %s 정도고 난이도는 %s 그리고 플랫폼은 %s이고 추가적인 요청은 %s 야 " +
                        "응답은 제목(title), 간단한 내용(description), 평점(star), 나와의 매칭점수(matchingScore), 추천 이유(reasonForRecommendation)를 적어주고 " +
                        "응답은 순수 JSON 배열로 알려",
                userGamePreference.getPreferredGenres(),
                userGamePreference.getPlayTime(),
                userGamePreference.getPlayStyle(),
                userGamePreference.getDifficulty(),
                userGamePreference.getPlatform(),
                userGamePreference.getExtraRequest()
        );

        String recommendation = geminiService.getContents(prompt);
        log.info(recommendation);
        // 추천 JSON 문자열을 구조화된 객체로 파싱

        ObjectMapper objectMapper = new ObjectMapper();
        List<GameRecommendationResponseDto> gameRecommendations;
        try {
            // 응답 문자열에서 JSON 배열 추출
            String jsonArray = recommendation
                    .replaceAll("(?s)^.*?```json\\s*", "") // 시작 부분의 ```
                    .replaceAll("\\s*```\\s*$", "") // 끝 부분의 ```
                    .trim();

            gameRecommendations = objectMapper.readValue(jsonArray, new TypeReference<List<GameRecommendationResponseDto>>() {
            });
        } catch (Exception e) {
            throw new ApiException(ErrorCode.RECOMMENDATION_NOT_FOUND);
        }

        // GameRecommendHistory 엔티티 생성 및 저장
        List<GameRecommendHistory> gameRecommendHistories = new ArrayList<>();
        for (GameRecommendationResponseDto responseDto : gameRecommendations) {
            GameRecommendHistory history = new GameRecommendHistory(
                    loginUser,
                    responseDto.getTitle(),
                    responseDto.getDescription(),
                    responseDto.getMatchingScore(),
                    responseDto.getReasonForRecommendation(),
                    responseDto.getStar(),
                    saveData
            );
            gameRecommendHistories.add(history);
        }
        // 저장
        gameRecommendHistoryRepository.saveAll(gameRecommendHistories);

        return new UserGamePreferenceResponseDto(saveData, gameRecommendations);
    }

    public Page<GameRecommendHistorysResponseDto> getGameRecommendHistories(Long userId, User loginUser) {

        if (userId != loginUser.getId()){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        Pageable pageable = PageRequest.of(0, 15);
        Page<GameRecommendHistory> histories = gameRecommendHistoryRepository.findByUserId(userId, pageable);

       // return histories.map(gameRecommendHistory -> new GameRecommendHistorysResponseDto(gameRecommendHistory));
        return histories.map(GameRecommendHistorysResponseDto::new);

    }

}
