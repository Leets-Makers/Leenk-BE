package leets.leenk.domain.leenk.domain.service;

import java.time.LocalDateTime;
import leets.leenk.domain.leenk.application.dto.request.LeenkUpdateRequest;
import leets.leenk.domain.leenk.application.exception.MaxParticipantsTooLowException;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkUpdateService {

    public void updateLeenk(Leenk leenk, Location location, LeenkUpdateRequest request) {
        updateTitle(leenk, request.getTitle());
        updateContent(leenk, request.getContent());
        updateStartTime(leenk, request.getStartTime());
        updateMaxParticipants(leenk, request.getMaxParticipants());
        updatePlaceName(location, request.getPlaceName());
    }

    private void updateTitle(Leenk leenk, String title) {
        if (title != null && !title.isBlank()) {
            leenk.updateTitle(title);
        }
    }

    private void updateContent(Leenk leenk, String content) {
        if (content != null && !content.isBlank()) {
            leenk.updateContent(content);
        }
    }

    private void updateStartTime(Leenk leenk, LocalDateTime startTime) {
        if (startTime != null) {
            leenk.updateStartTime(startTime);
        }
    }

    private void updateMaxParticipants(Leenk leenk, Long maxParticipants) {
        if (maxParticipants == null) {
            return;
        }

        long currentCount = leenk.getCurrentParticipants();

        if (maxParticipants < currentCount) {
            throw new MaxParticipantsTooLowException();
        }

        leenk.updateMaxParticipants(maxParticipants);
    }

    private void updatePlaceName(Location location, String placeName) {
        if (location != null && placeName != null && !placeName.isBlank()) {
            location.updatePlaceName(placeName);
        }
    }
}
