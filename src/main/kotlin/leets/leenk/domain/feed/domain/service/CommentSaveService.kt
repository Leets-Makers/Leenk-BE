package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.repository.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentSaveService(
    private val commentRepository: CommentRepository,
) {
    fun saveComment(comment: Comment) {
        commentRepository.save(comment)
    }
}
