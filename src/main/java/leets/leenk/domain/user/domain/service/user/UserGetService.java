package leets.leenk.domain.user.domain.service.user;

import leets.leenk.domain.user.application.exception.UserNotFoundException;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public User findById(long userId) {
        return userRepository.findByIdAndLeaveDateIsNullAndDeleteDateIsNull(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> existById(long userId) {
        return userRepository.findById(userId);
    }

    public List<User> findAll(List<Long> userIds) {
        return userRepository.findAllByIdInAndLeaveDateIsNullAndDeleteDateIsNull(userIds);
    }

    public List<User> findAll() {
        return userRepository.findAllByLeaveDateIsNullAndDeleteDateIsNullOrderByName();
    }

    public Slice<User> findAll(Pageable pageable) {
        return userRepository.findAllByLeaveDateIsNullAndDeleteDateIsNullOrderByName(pageable);
    }

    public List<User> findAllDeleteUser(LocalDateTime threshold) {
        return userRepository.findByDeleteDateIsNullAndLeaveDateBefore(threshold);
    }

    public User findByEmail(String email) {
        return userRepository.findByName("마스터")
                .orElseThrow(UserNotFoundException::new);
    }

    public List<User> findAllByBirthday(LocalDate today){
        return userRepository.findAllByBirthday(today);
    }
}
