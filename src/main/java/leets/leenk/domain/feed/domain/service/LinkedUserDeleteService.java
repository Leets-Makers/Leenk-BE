package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkedUserDeleteService {

    private final LinkedUserRepository linkedUserRepository;

    @Transactional
    public void deleteAllByFeed(Feed feed) {
        linkedUserRepository.deleteAllByFeed(feed);
        linkedUserRepository.flush();
    }
}
