package leets.leenk.domain.leenk.domain.service;

import java.time.LocalDateTime;
import leets.leenk.domain.leenk.application.exception.MaxParticipantsTooLowException;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.media.application.mapper.MediaMapper;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.service.MediaDeleteService;
import leets.leenk.domain.media.domain.service.MediaSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkUpdateService {

    private final MediaSaveService mediaSaveService;
    private final MediaDeleteService mediaDeleteService;

    private final MediaMapper mediaMapper;

    public void updateTitle(Leenk leenk, String title) {
        if (title != null && !title.isBlank()) {
            leenk.updateTitle(title);
        }
    }

    public void updateContent(Leenk leenk, String content) {
        if (content != null && !content.isBlank()) {
            leenk.updateContent(content);
        }
    }

    public void updateStartTime(Leenk leenk, LocalDateTime startTime) {
        if (startTime != null) {
            leenk.updateStartTime(startTime);
        }
    }

    public void updateMaxParticipants(Leenk leenk, Long maxParticipants) {
        if (maxParticipants == null) {
            return;
        }

        long currentCount = leenk.getCurrentParticipants();

        if (maxParticipants < currentCount) {
            throw new MaxParticipantsTooLowException();
        }

        leenk.updateMaxParticipants(maxParticipants);
    }

    public void updatePlaceName(Location location, String placeName) {
        if (location != null && placeName != null && !placeName.isBlank()) {
            location.updatePlaceName(placeName);
        }
    }

    public void updateMediaUrl(Leenk leenk, Media media, String newUrl) {
        if (newUrl == null || newUrl.isBlank()) {
            if (media != null) {
                mediaDeleteService.delete(media);
            }
            return;
        }

        if (media != null) {
            media.updateMediaUrl(newUrl);
        } else {
            Media newMedia = mediaMapper.toMedia(leenk, newUrl);
            mediaSaveService.save(newMedia);
        }
    }
}
