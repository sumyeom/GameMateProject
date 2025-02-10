package com.example.gamemate.domain.game.service;

import com.example.gamemate.domain.game.dto.request.ChatRequestDto;
import com.example.gamemate.domain.game.dto.response.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {
    @Qualifier("geminiRestTemplate")

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Gemini API에 프롬프트를 전송하고 응답을 받아옵니다.
     * @param prompt Gemini API에 전송할 프롬프트 문자열
     * @return Gemini API로부터 받은 응답 메시지
     */
    public String getContents(String prompt) {


        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        ChatRequestDto request = new ChatRequestDto(prompt);
        ChatResponseDto response = restTemplate.postForObject(requestUrl, request, ChatResponseDto.class);

        String message = response.getCandidates().get(0).getContent().getParts().get(0).getText().toString();

        return message;

    }
}
