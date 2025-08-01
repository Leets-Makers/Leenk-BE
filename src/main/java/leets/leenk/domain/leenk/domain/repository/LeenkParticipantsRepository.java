package leets.leenk.domain.leenk.domain.repository;

import java.util.List;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeenkParticipantsRepository extends JpaRepository<LeenkParticipants, Long> {

    List<LeenkParticipants> findAllByLeenk(Leenk leenk);
}
