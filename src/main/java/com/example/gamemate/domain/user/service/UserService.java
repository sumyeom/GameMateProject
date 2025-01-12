package com.example.gamemate.domain.user.service;

import com.example.gamemate.domain.auth.service.AuthService;
import com.example.gamemate.domain.user.dto.ProfileResponseDto;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public ProfileResponseDto findProfile(Long id, String token) {

        validateToken(token);

        User findUser = userRepository.findById(id)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(UserStatus.WITHDRAW.equals(findUser.getUserStatus())) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }
        return new ProfileResponseDto(findUser);
    }

    public ProfileResponseDto updateProfile(Long id, String newNickname, String token) {

        validateToken(token);

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateOwner(findUser, token);

        findUser.updateProfile(newNickname);
        User savedUser = userRepository.save(findUser);

        return new ProfileResponseDto(savedUser);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword, String token) {

       validateToken(token);

        User findUser = userRepository.findById(id)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateOwner(findUser, token);

        if(!passwordEncoder.matches(oldPassword, findUser.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        findUser.updatePassword(encodedPassword);
        userRepository.save(findUser);
    }

    public void withdrawUser(String token) {

        validateToken(token);

        String email = jwtTokenProvider.getEmailFromToken(token);
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(UserStatus.WITHDRAW.equals(findUser.getUserStatus())) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }

        findUser.deleteSoftly();
        findUser.updateUserStatus(UserStatus.WITHDRAW);
        findUser.removeRefreshToken();

        userRepository.save(findUser);

    }

    private void validateToken(String token) {
        if(!jwtTokenProvider.validateToken(token)) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    private void validateOwner(User user, String token) {
        String emailFromToken = jwtTokenProvider.getEmailFromToken(token);
        if(!user.getEmail().equals(emailFromToken)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }

}
