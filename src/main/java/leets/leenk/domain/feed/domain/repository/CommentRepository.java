package leets.leenk.domain.feed.domain.repository;

import leets.leenk.domain.feed.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
