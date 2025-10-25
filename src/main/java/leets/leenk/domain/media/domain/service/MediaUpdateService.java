package leets.leenk.domain.media.domain.service;

import leets.leenk.domain.media.domain.entity.Media;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaUpdateService {

    public void update(Media media, String thumbnailUrl){
        media.updateThumbnailUrl(thumbnailUrl);
    }
}
