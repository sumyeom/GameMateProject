package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.response.GameRecommendHistorysResponseDto;
import com.example.gamemate.domain.game.dto.response.GameRecommendationResponseDto;
import com.example.gamemate.domain.game.dto.request.UserGamePreferenceRequestDto;
import com.example.gamemate.domain.game.dto.response.UserGamePreferenceResponseDto;
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

    /**
     * 사용자의 게임 선호도를 기반으로 게임을 추천하고, 그 결과를 저장합니다.
     * @param requestDto 사용자의 게임 선호도 정보를 담은 DTO
     * @param loginUser 현재 로그인한 사용자
     * @return 사용자의 게임 선호도와 추천된 게임 목록을 포함한 응답 DTO
     */
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

        String prompt = String.format(
                "나에게 맞는 게임 3개 추천해줘 선호하는 장르는 %s이고 플레이 스타일은 %s 정도고 플레이 타임은 %s 정도고 난이도는 %s 그리고 플랫폼은 %s이고 추가적인 요청은 %s 야 " +
                        "응답은 " +
                        "한글(영어)로 된 제목(title), " +
                        "간단한 내용(description)," +
                        "metacriticScore 점수(metacriticScore)," +
                        //"나와의 매칭점수(matchingScore)," +
                        "추천 이유(reasonForRecommendation)를 적어주고 " +
                        "응답은 순수 JSON 배열로 알려줘",
                userGamePreference.getPreferredGenres(),
                userGamePreference.getPlayStyle(),
                userGamePreference.getPlayTime(),
                userGamePreference.getDifficulty(),
                userGamePreference.getPlatform(),
                userGamePreference.getExtraRequest()
        );

        String recommendation = geminiService.getContents(prompt);
        log.info(recommendation);


        ObjectMapper objectMapper = new ObjectMapper();
        List<GameRecommendationResponseDto> gameRecommendations;
        try {

            String jsonArray = recommendation
                    .replaceAll("(?s)^.*?```json\\s*", "") // 시작 부분의 ```
                    .replaceAll("\\s*```\\s*$", "") // 끝 부분의 ```
                    .trim();

            gameRecommendations = objectMapper.readValue(jsonArray, new TypeReference<List<GameRecommendationResponseDto>>() {
            });
        } catch (Exception e) {
            throw new ApiException(ErrorCode.RECOMMENDATION_NOT_FOUND);
        }

        List<GameRecommendHistory> gameRecommendHistories = new ArrayList<>();
        for (GameRecommendationResponseDto responseDto : gameRecommendations) {
            GameRecommendHistory history = new GameRecommendHistory(
                    loginUser,
                    responseDto.getTitle(),
                    responseDto.getDescription(),
                    //responseDto.getMatchingScore(),
                    responseDto.getReasonForRecommendation(),
                    responseDto.getMetacriticScore(),
                    saveData
            );
            gameRecommendHistories.add(history);
        }

        gameRecommendHistoryRepository.saveAll(gameRecommendHistories);

        return new UserGamePreferenceResponseDto(saveData, gameRecommendations);
    }

    /**
     * 로그인한 사용자의 게임 추천 기록을 페이지네이션하여 조회합니다.
     * @param loginUser 현재 로그인한 사용자
     * @return 사용자의 게임 추천 기록 목록 (페이지네이션 적용)
     */
    public Page<GameRecommendHistorysResponseDto> getGameRecommendHistories( User loginUser) {

        Pageable pageable = PageRequest.of(0, 15);
        Page<GameRecommendHistory> histories = gameRecommendHistoryRepository.findByUserId(loginUser.getId(), pageable);

        return histories.map(GameRecommendHistorysResponseDto::new);

    }

}
