package leets.leenk.domain.feed.domain.repository;

import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(Feed feed);
}
