package leets.leenk.domain.leenk.domain.service;

import java.time.LocalDateTime;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.media.domain.entity.Media;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkUpdateService {

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
        if (maxParticipants != null) {
            leenk.updateMaxParticipants(maxParticipants);
        }
    }

    public void updatePlaceName(Location location, String placeName) {
        if (location != null && placeName != null && !placeName.isBlank()) {
            location.updatePlaceName(placeName);
        }
    }

    public void updateMediaUrl(Media media, String newUrl) {
        if (media != null && newUrl != null && !newUrl.isBlank()) {
            media.updateMediaUrl(newUrl);
        }
    }
}
