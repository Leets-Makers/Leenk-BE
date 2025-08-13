package leets.leenk.domain.leenk.domain.service;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.repository.LeenkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkDeleteService {

    private final LeenkRepository leenkRepository;

    public void delete(Leenk leenk) {
        leenkRepository.delete(leenk);
    }
}
