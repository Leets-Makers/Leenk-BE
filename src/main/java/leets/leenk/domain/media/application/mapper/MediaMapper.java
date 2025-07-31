package leets.leenk.domain.media.application.mapper;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.entity.enums.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {

    public Media toMedia(Feed feed, FeedMediaRequest request) {
        return Media.builder()
                .feed(feed)
                .position(request.position())
                .mediaUrl(request.mediaUrl())
                .mediaType(request.mediaType())
                .build();
    }

    public Media toMedia(Leenk leenk, String url, int position) {
        return Media.builder()
                .leenk(leenk)
                .mediaUrl(url)
                .mediaType(MediaType.IMAGE)
                .position(position)
                .build();
    }
}
