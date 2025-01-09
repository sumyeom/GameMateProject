package com.example.gamemate.domain.auth.service;

import com.example.gamemate.domain.auth.dto.EmailLoginRequestDto;
import com.example.gamemate.domain.auth.dto.EmailLoginResponseDto;
import com.example.gamemate.domain.auth.dto.SignupRequestDto;
import com.example.gamemate.domain.auth.dto.SignupResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        Optional<User> findUser = userRepository.findByEmail(requestDto.getEmail());
        if(findUser.isPresent()) {
            if(findUser.get().getUserStatus() == UserStatus.WITHDRAW) {
                throw new ApiException(ErrorCode.WITHDRAWN_USER);
            }
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

        String rawPassword = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(requestDto.getEmail(), requestDto.getName(), requestDto.getNickname(), encodedPassword);
        User savedUser = userRepository.save(user);

        return new SignupResponseDto(savedUser);
    }

    public EmailLoginResponseDto emailLogin(EmailLoginRequestDto requestDto) {

        User findUser = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(findUser.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.WITHDRAWN_USER);
        }

        if(!passwordEncoder.matches(requestDto.getPassword(), findUser.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String jwtToken = jwtTokenProvider.createAccessToken(findUser.getEmail(), findUser.getRole());

        //Todo 로그인응답dto에서 토큰만 주면 되나?
        return new EmailLoginResponseDto(jwtToken, findUser.getEmail(), findUser.getNickname());
    }

}
