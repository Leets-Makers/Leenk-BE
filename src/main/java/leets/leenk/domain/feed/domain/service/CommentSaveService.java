package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentSaveService {
    private final CommentRepository commentRepository;

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }
}
