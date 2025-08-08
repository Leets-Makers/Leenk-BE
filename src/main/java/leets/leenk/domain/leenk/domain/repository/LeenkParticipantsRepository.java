package leets.leenk.domain.leenk.domain.repository;

import java.util.List;
import java.util.Optional;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeenkParticipantsRepository extends JpaRepository<LeenkParticipants, Long> {

    List<LeenkParticipants> findAllByLeenk(Leenk leenk);

    boolean existsByLeenkAndParticipant(Leenk leenk, User user);

    Optional<LeenkParticipants> findByLeenkIdAndParticipantId(Long leenkId, Long participantId);
}
