package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.exception.FeedNotFoundException;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.FeedRepository;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedGetService {

    private final FeedRepository feedRepository;

    public Feed findById(long feedId) {
        return feedRepository.findByDeletedAtIsNullAndId(feedId)
                .orElseThrow(FeedNotFoundException::new);
    }

    public Slice<Feed> findAll(Pageable pageable, List<UserBlock> blockedUser) {
        List<Long> blockedUserIds = blockedUser.stream()
                .map(UserBlock::getBlocked)
                .map(User::getId)
                .toList();

        return feedRepository.findAllByDeletedAtIsNullWithUser(pageable, blockedUserIds);
    }

    public Slice<Feed> findAllByUser(User user, Pageable pageable) {
        return feedRepository.findAllByUserAndDeletedAtIsNull(user, pageable);
    }
}
