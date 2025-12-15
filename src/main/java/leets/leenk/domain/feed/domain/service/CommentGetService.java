package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.exception.CommentNotFoundException;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentGetService {
    private final CommentRepository commentRepository;

    public Comment findCommentByIdNotDeleted(long commentId) {
        return commentRepository.findByCommentIdAndDeletedAtIsNull(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    public List<Comment> findAllByFeed(Feed feed) {
        return commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed);
    }
}
