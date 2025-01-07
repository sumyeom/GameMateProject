package com.example.gamemate.domain.user.service;

import com.example.gamemate.domain.user.dto.ProfileResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ProfileResponseDto findProfile(Long id) {
        User findUser = userRepository.findById(id).orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(UserStatus.WITHDRAW.equals(findUser.getUserStatus())) {
            throw new ApiException(ErrorCode.USER_WITHDRAWN);
        }
        return new ProfileResponseDto(findUser);
    }

    public ProfileResponseDto updateProfile(Long id, String newNickname) {
        User findUser = userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        findUser.updateProfile(newNickname);
        User savedUser = userRepository.save(findUser);

        return new ProfileResponseDto(savedUser);
    }

    public void updatePassword(Long id, String newPassword) {
        User findUser = userRepository.findById(id).orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        //Todo 비밀번호 검증 로직 Spring Security로 구현

        findUser.updatePassword(newPassword);
        User savedUser = userRepository.save(findUser);
    }

}
