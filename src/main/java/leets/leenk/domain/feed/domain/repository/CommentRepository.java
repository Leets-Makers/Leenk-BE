package leets.leenk.domain.feed.domain.repository;

import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentIdAndDeletedAtIsNull(long id);

    List<Comment> findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(Feed feed);
}
