package leets.leenk.domain.media.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.media.domain.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MediaDeleteService {

    private final MediaRepository mediaRepository;

    @Transactional
    public void deleteAllByFeed(Feed feed) {
        mediaRepository.deleteAllByFeed(feed);
        mediaRepository.flush();
    }
}
