package leets.leenk.domain.leenk.domain.service;

import java.util.List;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeenkParticipantsDeleteService {

    private final LeenkParticipantsRepository participantsRepository;

    @Transactional
    public void delete(LeenkParticipants participants) {
        participantsRepository.delete(participants);
    }

    @Transactional
    public void deleteAll(List<LeenkParticipants> participants) {
        participantsRepository.deleteAll(participants);
    }
}
