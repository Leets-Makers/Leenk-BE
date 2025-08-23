package leets.leenk.domain.leenk.domain.service;

import java.util.List;
import leets.leenk.domain.leenk.application.exception.LeenkParticipantNotFoundException;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkParticipantsGetService {

    private final LeenkParticipantsRepository leenkParticipantsRepository;

    public List<LeenkParticipants> findAllByLeenk(Leenk leenk) {

        return leenkParticipantsRepository.findAllByLeenk(leenk);
    }

    public Slice<LeenkParticipants> findSliceByParticipant(User user, Pageable pageable) {
        return leenkParticipantsRepository.findAllByParticipantOrderByJoinedAtDesc(user, pageable);
    }

    public boolean existsByLeenkAndParticipant(Leenk leenk, User user) {
        return leenkParticipantsRepository.existsByLeenkAndParticipant(leenk, user);
    }

    public LeenkParticipants findByLeenkAndParticipantId(Long leenkId, Long participantId) {

        return leenkParticipantsRepository.findByLeenkIdAndParticipantId(leenkId, participantId)
                .orElseThrow(LeenkParticipantNotFoundException::new);
    }
}
