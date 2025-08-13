package leets.leenk.domain.media.domain.service;

import java.util.List;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
