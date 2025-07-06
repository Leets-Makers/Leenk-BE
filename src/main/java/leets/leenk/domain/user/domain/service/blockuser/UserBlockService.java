package leets.leenk.domain.user.domain.service.blockuser;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
import leets.leenk.domain.user.domain.repository.BlockedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final BlockedUserRepository blockedUserRepository;

    public void blockUser(UserBlock userBlock) {
        blockedUserRepository.save(userBlock);
    }

    public List<UserBlock> findAllByBlocker(User blocker) {
        return blockedUserRepository.findAllByBlocker(blocker);
    }

    public boolean isAlreadyBlocked(User user, User blockedUser) {
        return blockedUserRepository.findByBlockerAndBlocked(user, blockedUser).isPresent();
    }
}
