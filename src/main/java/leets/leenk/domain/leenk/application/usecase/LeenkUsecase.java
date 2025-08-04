package leets.leenk.domain.leenk.application.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse;
import leets.leenk.domain.leenk.application.exception.AlreadyParticipatedException;
import leets.leenk.domain.leenk.application.exception.CannotKickSelfException;
import leets.leenk.domain.leenk.application.exception.LeenkAlreadyClosedException;
import leets.leenk.domain.leenk.application.exception.LeenkNotRecruitingException;
import leets.leenk.domain.leenk.application.exception.MaxParticipantsExceededException;
import leets.leenk.domain.leenk.application.exception.NotLeenkOwnerException;
import leets.leenk.domain.leenk.application.mapper.LeenkMapper;
import leets.leenk.domain.leenk.application.mapper.LeenkParticipantsMapper;
import leets.leenk.domain.leenk.application.mapper.LocationMapper;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import leets.leenk.domain.leenk.domain.service.LeenkGetService;
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsDeleteService;
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsGetService;
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsSaveService;
import leets.leenk.domain.leenk.domain.service.LeenkSaveService;
import leets.leenk.domain.leenk.domain.service.LocationSaveService;
import leets.leenk.domain.media.application.mapper.MediaMapper;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.service.MediaGetService;
import leets.leenk.domain.media.domain.service.MediaSaveService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LeenkUsecase {

    private final LocationSaveService locationSaveService;
    private final LeenkSaveService leenkSaveService;
    private final LeenkParticipantsSaveService leenkParticipantsSaveService;
    private final MediaSaveService mediaSaveService;
    private final UserGetService userGetService;
    private final LeenkGetService leenkGetService;
    private final LeenkParticipantsGetService leenkParticipantsGetService;
    private final MediaGetService mediaGetService;
    private final LeenkParticipantsDeleteService leenkParticipantsDeleteService;
    private final LeenkMapper leenkMapper;
    private final LeenkParticipantsMapper participantsMapper;
    private final LocationMapper locationMapper;
    private final MediaMapper mediaMapper;

    @Transactional
    public void uploadLeenk(Long userId, LeenkUploadRequest request) {
        User author = userGetService.findById(userId);

        Location location = locationMapper.toLocation(request.placeName());
        locationSaveService.save(location);

        Leenk leenk = leenkMapper.toLeenk(author, location, request);
        leenkSaveService.save(leenk);

        LeenkParticipants self = participantsMapper.toParticipants(leenk, author, LocalDateTime.now());
        leenkParticipantsSaveService.save(self);

        Optional.ofNullable(request.mediaUrl())
                .filter(StringUtils::hasText)
                .ifPresent(url -> {
                    Media media = mediaMapper.toMedia(leenk, url);
                    mediaSaveService.save(media);
                });
    }

    @Transactional(readOnly = true)
    public LeenkListResponse getLeenks(Long userId, LeenkFilter status, int pageNumber, int pageSize) {
        userGetService.findById(userId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createDate").descending());

        Slice<Leenk> slice = leenkGetService.findByStatusParam(status, pageable);

        List<Leenk> leenks = slice.getContent();
        List<Media> medias = mediaGetService.findByLeenks(leenks);
        Map<Long, List<Media>> mediaMap = medias.stream()
                .collect(Collectors.groupingBy(m -> m.getLeenk().getId()));

        return leenkMapper.toLeenkListResponse(slice, mediaMap);
    }

    @Transactional(readOnly = true)
    public LeenkDetailResponse getLeenkDetail(Long leenkId) {
        Leenk leenk = leenkGetService.findById(leenkId);
        String mediaUrl = mediaGetService.findMediaUrlByLeenk(leenk);

        return leenkMapper.toLeenkDetailResponse(leenk, mediaUrl);
    }

    @Transactional(readOnly = true)
    public LeenkParticipantsListResponse getLeenkParticipants(Long leenkId) {
        Leenk leenk = leenkGetService.findById(leenkId);

        if (leenk.getStatus() != LeenkStatus.RECRUITING) {
            throw new LeenkNotRecruitingException();
        }

        List<LeenkParticipants> participants = leenkParticipantsGetService.findAllByLeenk(leenk);
        return participantsMapper.toLeenkParticipantsListResponse(leenk, participants);
    }

    @Transactional
    public void participateLeenk(Long userId, Long leenkId) {
        User user = userGetService.findById(userId);
        Leenk leenk = leenkGetService.findById(leenkId);

        if (leenk.getStatus() != LeenkStatus.RECRUITING) {
            throw new LeenkNotRecruitingException();
        }

        boolean alreadyJoined = leenkParticipantsGetService.existsByLeenkAndParticipant(leenk, user);
        if (alreadyJoined) {
            throw new AlreadyParticipatedException();
        }

        if (leenk.getCurrentParticipants() >= leenk.getMaxParticipants()) {
            throw new MaxParticipantsExceededException();
        }

        LeenkParticipants participant = participantsMapper.toParticipants(leenk, user, LocalDateTime.now());

        leenkParticipantsSaveService.save(participant);
        leenk.increaseCurrentParticipants();
    }

    @Transactional
    public void kickParticipant(Long userId, Long leenkId, Long participantId) {
        Leenk leenk = leenkGetService.findById(leenkId);

        if (leenk.getStatus() != LeenkStatus.RECRUITING) {
            throw new LeenkNotRecruitingException();
        }

        if (!leenk.getAuthor().getId().equals(userId)) {
            throw new NotLeenkOwnerException();
        }

        if (userId.equals(participantId)) {
            throw new CannotKickSelfException();
        }

        LeenkParticipants participant = leenkParticipantsGetService.findByLeenkAndParticipantId(leenk.getId(),
                participantId);

        leenkParticipantsDeleteService.delete(participant);
        leenk.decreaseCurrentParticipants();
    }

    @Transactional
    public void closeLeenk(Long userId, Long leenkId) {
        userGetService.findById(userId);
        Leenk leenk = leenkGetService.findById(leenkId);

        if (!leenk.getAuthor().getId().equals(userId)) {
            throw new NotLeenkOwnerException();
        }

        if (leenk.getStatus() != LeenkStatus.RECRUITING) {
            throw new LeenkAlreadyClosedException();
        }

        leenk.changeStatusToClosed();
    }
}
