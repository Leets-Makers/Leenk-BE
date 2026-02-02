package leets.leenk.domain.leenk.application.mapper;

import java.util.List;
import java.util.Map;

import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.dto.response.LeenkAuthorResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkResponse;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeenkMapper {
    private final UserProfileMapper userProfileMapper;

    public Leenk toLeenk(User author, Location location, LeenkUploadRequest request) {
        return Leenk.builder()
                .author(author)
                .location(location)
                .title(request.getTitle())
                .content(request.getContent())
                .startTime(request.getStartTime())
                .maxParticipants(request.getMaxParticipants())
                .build();
    }

    public LeenkAuthorResponse toLeenkAuthorResponse(Leenk leenk) {
        return LeenkAuthorResponse.builder()
                .user(userProfileMapper.toProfile(leenk.getAuthor()))
                .build();
    }

    public LeenkListResponse toLeenkListResponse(Slice<Leenk> slice, Map<Long, List<Media>> mediaMap) {
        List<LeenkResponse> responses = toLeenkResponses(slice.getContent(), mediaMap);

        return LeenkListResponse.builder()
                .leenks(responses)
                .pageable(PageableMapperUtil.from(slice))
                .build();
    }

    public LeenkDetailResponse toLeenkDetailResponse(Leenk leenk, String mediaUrl, boolean isParticipated) {

        return LeenkDetailResponse.builder()
                .id(leenk.getId())
                .author(toLeenkAuthorResponse(leenk))
                .kakaoId(leenk.getAuthor().getKakaoTalkId())
                .status(leenk.getStatus())
                .title(leenk.getTitle())
                .placeName(leenk.getLocation().getPlaceName())
                .currentParticipants(leenk.getCurrentParticipants())
                .maxParticipants(leenk.getMaxParticipants())
                .startTime(leenk.getStartTime())
                .content(leenk.getContent())
                .mediaUrl(mediaUrl)
                .createdAt(leenk.getCreateDate())
                .updatedAt(leenk.getUpdateDate())
                .isParticipated(isParticipated)
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
        String imageUrl = (representative != null) ? representative.getThumbnailUrl() : null;

        return LeenkResponse.builder()
                .leenkId(leenk.getId())
                .author(toLeenkAuthorResponse(leenk))
                .title(leenk.getTitle())
                .currentParticipants(leenk.getCurrentParticipants())
                .maxParticipants(leenk.getMaxParticipants())
                .startTime(leenk.getStartTime())
                .createdAt(leenk.getCreateDate())
                .updatedAt(leenk.getUpdateDate())
                .thumbNail(imageUrl)
                .build();
    }
}
