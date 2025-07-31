package leets.leenk.domain.leenk.domain.repository;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeenkRepository extends JpaRepository<Leenk, Long> {

}
