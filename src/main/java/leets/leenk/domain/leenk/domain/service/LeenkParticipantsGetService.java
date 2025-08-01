package leets.leenk.domain.leenk.domain.service;

import java.util.List;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkParticipantsGetService {

    private final LeenkParticipantsRepository leenkParticipantsRepository;

    public List<LeenkParticipants> findAllByLeenk(Leenk leenk) {

        return leenkParticipantsRepository.findAllByLeenk(leenk);
    }
}
