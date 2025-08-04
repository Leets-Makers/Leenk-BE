package leets.leenk.domain.media.domain.service;

import java.util.List;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.media.application.exception.MediaNotFoundException;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaGetService {

    private final MediaRepository mediaRepository;

    public Media findById(long mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(MediaNotFoundException::new);
    }

    public List<Media> findAllByFeed(Feed feed) {
        return mediaRepository.findAllByFeedOrderByPosition(feed);
    }

    public List<Media> findAllByFeeds(List<Feed> feeds) {
        return mediaRepository.findAllByFeedInOrderByPosition(feeds);
    }

    public String findMediaUrlByLeenk(Leenk leenk) {
        return mediaRepository
                .findFirstByLeenkOrderByPositionAsc(leenk)
                .map(Media::getMediaUrl)
                .orElse("");
    }

    public List<Media> findByLeenks(List<Leenk> leenks) {
        return mediaRepository.findAllByLeenkInOrderByPosition(leenks);
    }
}
