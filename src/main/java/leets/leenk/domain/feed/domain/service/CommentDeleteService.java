package leets.leenk.domain.feed.domain.service;


import leets.leenk.domain.feed.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    public void deleteComment(Comment comment) {
        comment.deleteComment();
    }
}
