package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.feed.domain.repository.ReactionRepository
import org.springframework.stereotype.Service

@Service
class ReactionSaveService(
    private val reactionRepository: ReactionRepository,
) {
    fun save(reaction: Reaction): Reaction = reactionRepository.save(reaction)
}
