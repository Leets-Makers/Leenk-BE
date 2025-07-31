package leets.leenk.domain.leenk.application.usecase;

import java.util.List;
import java.util.stream.IntStream;
import leets.leenk.domain.leenk.application.dto.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.mapper.LeenkMapper;
import leets.leenk.domain.leenk.application.mapper.LeenkParticipantsMapper;
import leets.leenk.domain.leenk.application.mapper.LocationMapper;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsSaveService;
import leets.leenk.domain.leenk.domain.service.LeenkSaveService;
import leets.leenk.domain.leenk.domain.service.LocationSaveService;
import leets.leenk.domain.media.application.mapper.MediaMapper;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.service.MediaSaveService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeenkUsecase {

    private static final int START_POSITION = 0;
    private final UserGetService userGetService;
    private final LocationSaveService locationSaveService;
    private final LeenkSaveService leenkSaveService;
    private final LeenkParticipantsSaveService leenkParticipantsSaveService;
    private final MediaSaveService mediaSaveService;
    private final LeenkMapper leenkMapper;
    private final LeenkParticipantsMapper participantsMapper;
    private final LocationMapper locationMapper;
    private final MediaMapper mediaMapper;

    @Transactional
    public void uploadLeenk(Long userId, LeenkUploadRequest request) {
        User author = userGetService.findById(userId);

        Location location = locationMapper.toLocation(request.place());
        locationSaveService.save(location);

        Leenk leenk = leenkMapper.toLeenk(author, location, request);
        leenkSaveService.save(leenk);

        LeenkParticipants self = participantsMapper.toParticipants(leenk, author);
        leenkParticipantsSaveService.save(self);

        List<Media> images = IntStream.range(START_POSITION, request.imageUrls().size())
                .mapToObj(i -> mediaMapper.toMedia(leenk, request.imageUrls().get(i), i))
                .toList();
        mediaSaveService.saveAll(images);
    }
}
