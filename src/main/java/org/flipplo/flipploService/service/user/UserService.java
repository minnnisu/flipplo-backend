package org.flipplo.flipploService.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flipplo.flipploService.config.ErrorCode;
import org.flipplo.flipploService.dto.user.UserInfo;
import org.flipplo.flipploService.user.User;
import org.flipplo.flipploService.exception.CustomErrorException;
import org.flipplo.flipploService.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserInfo.Dto getUserInfo(User user) {
        if(user == null) {
            throw new CustomErrorException(ErrorCode.UserNotFoundError);
        }

        return UserInfo.Dto.fromEntity(user);
    }
}
