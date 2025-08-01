package leets.leenk.domain.leenk.application.mapper;

import java.util.List;
import java.util.Map;
import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkResponse;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import org.springframework.data.domain.Slice;
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

    public LeenkListResponse toLeenkListResponse(Slice<Leenk> slice, Map<Long, List<Media>> mediaMap) {
        List<LeenkResponse> responses = toLeenkResponses(slice.getContent(), mediaMap);

        return LeenkListResponse.builder()
                .leenks(responses)
                .pageable(PageableMapperUtil.from(slice))
                .build();
    }

    public LeenkDetailResponse toLeenkDetailResponse(Leenk leenk, List<Media> medias) {
        List<String> imageUrls = medias.stream()
                .map(Media::getMediaUrl)
                .toList();

        return LeenkDetailResponse.builder()
                .id(leenk.getId())
                .userId(leenk.getAuthor().getId())
                .userName(leenk.getAuthor().getName())
                .userProfileImage(leenk.getAuthor().getProfileImage())
                .title(leenk.getTitle())
                .placeName(leenk.getLocation().getPlaceName())
                .currentParticipants(leenk.getCurrentParticipants())
                .maxParticipants(leenk.getMaxParticipants())
                .startTime(leenk.getStartTime())
                .content(leenk.getContent())
                .images(imageUrls)
                .createdAt(leenk.getCreateDate())
                .updatedAt(leenk.getUpdateDate())
                .build();
    }

    private List<LeenkResponse> toLeenkResponses(List<Leenk> leenks, Map<Long, List<Media>> mediaMap) {
        return leenks.stream()
                .map(leenk -> {
                    List<Media> medias = mediaMap.getOrDefault(leenk.getId(), List.of());
                    Media representative = medias.stream().findFirst()
                            .orElse(null);

                    return toLeenkResponse(leenk, representative);
                })
                .toList();
    }

    private LeenkResponse toLeenkResponse(Leenk leenk, Media representative) {
        String imageUrl = (representative != null) ? representative.getMediaUrl() : null;

        return LeenkResponse.builder()
                .leenkId(leenk.getId())
                .userId(leenk.getAuthor().getId())
                .title(leenk.getTitle())
                .currentParticipants(leenk.getCurrentParticipants())
                .maxParticipants(leenk.getMaxParticipants())
                .startTime(leenk.getStartTime())
                .createdAt(leenk.getCreateDate())
                .updatedAt(leenk.getUpdateDate())
                .representativeImage(imageUrl)
                .build();
    }
}
