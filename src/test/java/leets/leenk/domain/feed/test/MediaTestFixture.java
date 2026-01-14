package leets.leenk.domain.feed.test;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.entity.enums.MediaType;

public class MediaTestFixture {
    public static Media createMedia(Long id, Feed feed) {
        return Media.builder()
                .id(id)
                .feed(feed)
                .mediaUrl("https://example.com/media.jpg")
                .thumbnailUrl("https://example.com/media.jpg")
                .mediaType(MediaType.IMAGE)
                .position(1)
                .build();
    }
}
