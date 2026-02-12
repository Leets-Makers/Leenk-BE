package leets.leenk.domain.user.domain.service.user;

import leets.leenk.domain.user.application.exception.UserAlreadyExistsException;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSaveService {
    private final UserRepository userRepository;

    public void save(User user) {
        // appleId가 있는 경우 중복 체크 (기존 사용자 하위호환을 위함)
        if (user.getAppleId() != null && userRepository.existsByAppleId(user.getAppleId())) {
            throw new UserAlreadyExistsException();
        }
        userRepository.save(user);
    }
}
