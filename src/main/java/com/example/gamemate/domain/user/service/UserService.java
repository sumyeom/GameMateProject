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

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 프로필 조회, 프로필 수정, 비밀번호 변경, 회원 탈퇴 등의 작업을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    /**
     * 사용자의 프로필을 조회합니다.
     * @param id 조회할 사용자의 ID
     * @param loginUser 현재 로그인한 사용자
     * @return 조회된 사용자의 프로필 정보
     */
    @Transactional(readOnly = true)
    public ProfileResponseDto findProfile(Long id, User loginUser) {

        User findUser = userRepository.findById(id)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        if(UserStatus.WITHDRAW.equals(findUser.getUserStatus())) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }
        return new ProfileResponseDto(findUser);
    }

    /**
     * 사용자의 프로필을 수정합니다.
     * @param id 수정할 사용자의 ID
     * @param newNickname 새 닉네임
     * @param loginUser 현재 로그인한 사용자
     */
    public void updateProfile(Long id, String newNickname, User loginUser) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateOwner(findUser, loginUser);

        findUser.updateProfile(newNickname);
        userRepository.save(findUser);

    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     * @param id 변경할 사용자의 ID
     * @param oldPassword 기존 비밀번호
     * @param newPassword 새 비밀번호
     * @param loginUser 현재 로그인한 사용자
     */
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

    /**
     * 사용자의 탈퇴 요청을 처리합니다.
     * @param loginUser 현재 로그인한 사용자
     */
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

    /**
     * 사용자가 일치하는지 확인합니다.
     * @param user 확인할 사용자
     * @param loginUser 현재 로그인한 사용자
     */
    private void validateOwner(User user, User loginUser) {
        if(!user.getEmail().equals(loginUser.getEmail())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }

}
