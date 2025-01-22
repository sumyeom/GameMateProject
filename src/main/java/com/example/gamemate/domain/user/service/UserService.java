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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public ProfileResponseDto findProfile(Long id, User loginUser) {

        User findUser = userRepository.findById(id)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(UserStatus.WITHDRAW.equals(findUser.getUserStatus())) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }
        return new ProfileResponseDto(findUser);
    }

    public void updateProfile(Long id, String newNickname, User loginUser) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateOwner(findUser, loginUser);

        findUser.updateProfile(newNickname);
        User savedUser = userRepository.save(findUser);

    }

    public void updatePassword(Long id, String oldPassword, String newPassword, User loginUser) {

        User findUser = userRepository.findById(id)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateOwner(findUser, loginUser);

        if(!passwordEncoder.matches(oldPassword, findUser.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        findUser.updatePassword(encodedPassword);
        userRepository.save(findUser);
    }

    public void withdrawUser(User loginUser) {

        loginUser.updateUserStatus(UserStatus.WITHDRAW);
        loginUser.removeRefreshToken();

        userRepository.save(loginUser);

    }

//    private void validateToken(String token) {
//        if(!jwtTokenProvider.validateToken(token)) {
//            throw new ApiException(ErrorCode.INVALID_TOKEN);
//        }
//    }

    private void validateOwner(User user, User loginUser) {
        if(!user.getEmail().equals(loginUser.getEmail())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }

}
