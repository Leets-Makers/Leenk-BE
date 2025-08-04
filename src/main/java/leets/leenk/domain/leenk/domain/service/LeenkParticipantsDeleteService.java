package leets.leenk.domain.leenk.domain.service;

import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkParticipantsDeleteService {

    private final LeenkParticipantsRepository participantsRepository;

    public void delete(LeenkParticipants participants) {
        participantsRepository.delete(participants);
    }
}
