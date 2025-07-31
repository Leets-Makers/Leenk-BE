package leets.leenk.domain.leenk.application.mapper;

import leets.leenk.domain.leenk.application.dto.LeenkUploadRequest;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LeenkMapper {

    public Leenk toLeenk(User author, Location location, LeenkUploadRequest request) {
        return Leenk.builder()
                .author(author)
                .location(location)
                .title(request.title())
                .content(request.content())
                .startTime(request.meetingTime())
                .maxParticipants(request.maxParticipants())
                .build();
    }
}
