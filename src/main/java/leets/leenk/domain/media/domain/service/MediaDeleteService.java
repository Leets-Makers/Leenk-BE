package leets.leenk.domain.media.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaDeleteService {

    private final MediaRepository mediaRepository;

    public void delete(Media media) {
        mediaRepository.delete(media);
    }

    public void deleteAll(List<Media> mediaList) {
        mediaRepository.deleteAll(mediaList);
    }

    public void deleteAllByFeed(Feed feed) {
        mediaRepository.deleteAllByFeed(feed);
        mediaRepository.flush();
    }
}
