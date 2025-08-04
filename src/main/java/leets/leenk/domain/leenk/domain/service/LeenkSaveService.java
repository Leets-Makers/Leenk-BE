package leets.leenk.domain.leenk.domain.service;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.repository.LeenkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkSaveService {

    private final LeenkRepository leenkRepository;

    public void save(Leenk leenk) {
        leenkRepository.save(leenk);
    }
}
