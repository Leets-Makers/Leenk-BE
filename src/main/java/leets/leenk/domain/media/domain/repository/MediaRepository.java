package leets.leenk.domain.media.domain.repository;

import java.util.List;
import java.util.Optional;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.media.domain.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findAllByFeedInOrderByPosition(List<Feed> feeds);

    List<Media> findAllByFeedOrderByPosition(Feed feed);

    List<Media> findAllByLeenkOrderByPosition(Leenk leenk);

    Optional<Media> findFirstByLeenkOrderByPositionAsc(Leenk leenk);

    List<Media> findAllByLeenkInOrderByPosition(List<Leenk> leenks);
}
