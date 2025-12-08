package leets.leenk.domain.feed.domain.service;

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

    public List<Comment> findAllByFeed(Feed feed) {
        return commentRepository.findAllByFeed(feed);
    }
}
