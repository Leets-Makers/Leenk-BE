package leets.leenk.domain.leenk.domain.repository;

import leets.leenk.domain.leenk.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
