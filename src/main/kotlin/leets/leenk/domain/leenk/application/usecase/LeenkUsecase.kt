package leets.leenk.domain.leenk.application.usecase

import leets.leenk.domain.leenk.application.dto.request.LeenkReportRequest
import leets.leenk.domain.leenk.application.dto.request.LeenkUpdateRequest
import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest
import leets.leenk.domain.leenk.application.dto.response.LeenkCreateResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse
import leets.leenk.domain.leenk.application.exception.AlreadyParticipatedException
import leets.leenk.domain.leenk.application.exception.CannotKickSelfException
import leets.leenk.domain.leenk.application.exception.CannotLeaveAsHostException
import leets.leenk.domain.leenk.application.exception.LeenkAlreadyClosedException
import leets.leenk.domain.leenk.application.exception.LeenkAlreadyFinishedException
import leets.leenk.domain.leenk.application.exception.LeenkNotRecruitingException
import leets.leenk.domain.leenk.application.exception.LeenkParticipantNotFoundException
import leets.leenk.domain.leenk.application.exception.MaxParticipantsExceededException
import leets.leenk.domain.leenk.application.exception.NotLeenkOwnerException
import leets.leenk.domain.leenk.application.mapper.LeenkMapper
import leets.leenk.domain.leenk.application.mapper.LeenkParticipantsMapper
import leets.leenk.domain.leenk.application.mapper.LocationMapper
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.domain.service.LeenkDeleteService
import leets.leenk.domain.leenk.domain.service.LeenkGetService
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsDeleteService
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsGetService
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsSaveService
import leets.leenk.domain.leenk.domain.service.LeenkSaveService
import leets.leenk.domain.leenk.domain.service.LeenkUpdateService
import leets.leenk.domain.leenk.domain.service.LocationSaveService
import leets.leenk.domain.media.application.mapper.MediaMapper
import leets.leenk.domain.media.domain.service.MediaDeleteService
import leets.leenk.domain.media.domain.service.MediaGetService
import leets.leenk.domain.media.domain.service.MediaSaveService
import leets.leenk.domain.notification.application.usecase.LeenkNotificationUsecase
import leets.leenk.domain.user.domain.service.NotionDatabaseService
import leets.leenk.domain.user.domain.service.SlackWebhookService
import leets.leenk.domain.user.domain.service.user.UserGetService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LeenkUsecase(
    private val locationSaveService: LocationSaveService,
    private val leenkSaveService: LeenkSaveService,
    private val leenkGetService: LeenkGetService,
    private val leenkUpdateService: LeenkUpdateService,
    private val leenkDeleteService: LeenkDeleteService,
    private val leenkParticipantsSaveService: LeenkParticipantsSaveService,
    private val leenkParticipantsGetService: LeenkParticipantsGetService,
    private val leenkParticipantsDeleteService: LeenkParticipantsDeleteService,
    private val mediaSaveService: MediaSaveService,
    private val mediaGetService: MediaGetService,
    private val mediaDeleteService: MediaDeleteService,
    private val userGetService: UserGetService,
    private val slackWebhookService: SlackWebhookService,
    private val notionDatabaseService: NotionDatabaseService,
    private val leenkMapper: LeenkMapper,
    private val participantsMapper: LeenkParticipantsMapper,
    private val locationMapper: LocationMapper,
    private val mediaMapper: MediaMapper,
    private val leenkNotificationUsecase: LeenkNotificationUsecase,
) {
    @Transactional
    fun uploadLeenk(
        userId: Long,
        request: LeenkUploadRequest,
    ): LeenkCreateResponse {
        val author = userGetService.findById(userId)

        val location = locationMapper.toLocation(request.placeName)
        locationSaveService.save(location)

        val leenk = leenkMapper.toLeenk(author, location, request)
        leenkSaveService.save(leenk)

        val hostParticipant = participantsMapper.toParticipants(leenk, author, LocalDateTime.now())
        leenkParticipantsSaveService.save(hostParticipant)

        request.mediaUrl?.takeIf { it.isNotBlank() }?.let { url ->
            val media = mediaMapper.toMedia(leenk, url)
            mediaSaveService.save(media)
        }

        leenkNotificationUsecase.saveNewLeenkNotification(leenk)

        return LeenkCreateResponse(leenk.id!!)
    }

    @Transactional
    fun updateLeenk(
        userId: Long,
        leenkId: Long,
        request: LeenkUpdateRequest,
    ) {
        userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)
        val location = leenk.location
        val media = mediaGetService.findFirstMediaByLeenk(leenk).orElse(null)

        if (leenk.author.id != userId) {
            throw NotLeenkOwnerException()
        }

        if (leenk.status != LeenkStatus.RECRUITING) {
            throw LeenkAlreadyClosedException()
        }

        leenkUpdateService.updateLeenk(leenk, location, request)

        val newUrl = request.mediaUrl
        if (newUrl.isNullOrBlank()) {
            media?.let { mediaDeleteService.delete(it) }
        } else {
            if (media != null) {
                media.updateMediaUrl(newUrl)
            } else {
                val newMedia = mediaMapper.toMedia(leenk, newUrl)
                mediaSaveService.save(newMedia)
            }
        }
    }

    @Transactional(readOnly = true)
    fun reportLeenk(
        userId: Long,
        leenkId: Long,
        request: LeenkReportRequest,
    ) {
        val user = userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)

        notionDatabaseService.sendLeenkReport(request.report, user.id, leenk.id)
        slackWebhookService.sendLeenkReport(request.report)
    }

    @Transactional(readOnly = true)
    fun getLeenks(
        userId: Long,
        status: LeenkFilter,
        pageNumber: Int,
        pageSize: Int,
    ): LeenkListResponse {
        userGetService.findById(userId)

        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createDate").descending())

        val slice = leenkGetService.findByStatusParam(status, pageable)

        val leenks = slice.content
        val medias = mediaGetService.findByLeenks(leenks)
        val mediaMap = medias.groupBy { it.leenk.id!! }

        return leenkMapper.toLeenkListResponse(slice, mediaMap)
    }

    @Transactional(readOnly = true)
    fun getLeenkDetail(
        userId: Long,
        leenkId: Long,
    ): LeenkDetailResponse {
        val leenk = leenkGetService.findById(leenkId)
        val mediaUrl = mediaGetService.findMediaUrlByLeenk(leenk)

        val user = userGetService.findById(userId)
        val isParticipated = leenkParticipantsGetService.existsByLeenkAndParticipant(leenk, user)

        return leenkMapper.toLeenkDetailResponse(leenk, mediaUrl, isParticipated)
    }

    @Transactional(readOnly = true)
    fun getLeenkParticipants(leenkId: Long): LeenkParticipantsListResponse {
        val leenk = leenkGetService.findById(leenkId)

        val participants = leenkParticipantsGetService.findAllByLeenk(leenk)
        return participantsMapper.toLeenkParticipantsListResponse(leenk, participants)
    }

    @Transactional(readOnly = true)
    fun getMyParticipatedLeenks(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): LeenkListResponse {
        val user = userGetService.findById(userId)
        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createDate").descending())
        val participantsSlice = leenkParticipantsGetService.findSliceByParticipant(user, pageable)

        val leenks = participantsSlice.content.map { it.leenk }
        val medias = mediaGetService.findByLeenks(leenks)
        val mediaMap = medias.groupBy { it.leenk.id!! }

        val leenkSlice = SliceImpl(leenks, pageable, participantsSlice.hasNext())

        return leenkMapper.toLeenkListResponse(leenkSlice, mediaMap)
    }

    @Transactional(readOnly = true)
    fun getUserParticipatedLeenks(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): LeenkListResponse {
        val user = userGetService.findById(userId)
        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("joinedAt").descending())
        val participantsSlice = leenkParticipantsGetService.findSliceByParticipant(user, pageable)

        val leenks = participantsSlice.content.map { it.leenk }
        val medias = mediaGetService.findByLeenks(leenks)
        val mediaMap = medias.groupBy { it.leenk.id!! }

        val leenkSlice = SliceImpl(leenks, pageable, participantsSlice.hasNext())

        return leenkMapper.toLeenkListResponse(leenkSlice, mediaMap)
    }

    @Transactional
    fun participateLeenk(
        userId: Long,
        leenkId: Long,
    ) {
        val user = userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)

        if (leenk.status != LeenkStatus.RECRUITING) {
            throw LeenkNotRecruitingException()
        }

        val alreadyJoined = leenkParticipantsGetService.existsByLeenkAndParticipant(leenk, user)
        if (alreadyJoined) {
            throw AlreadyParticipatedException()
        }

        if (leenk.currentParticipants >= leenk.maxParticipants) {
            throw MaxParticipantsExceededException()
        }

        val participant = participantsMapper.toParticipants(leenk, user, LocalDateTime.now())

        leenkParticipantsSaveService.save(participant)
        leenk.increaseCurrentParticipants()

        leenkNotificationUsecase.saveNewLeenkParticipantNotification(leenk, user)
    }

    @Transactional
    fun closeLeenk(
        userId: Long,
        leenkId: Long,
    ) {
        userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)

        if (leenk.author.id != userId) {
            throw NotLeenkOwnerException()
        }

        if (leenk.status != LeenkStatus.RECRUITING) {
            throw LeenkAlreadyClosedException()
        }

        leenk.changeStatusToClosed()

        leenkNotificationUsecase.saveLeenkClosedNotification(leenk)
    }

    @Transactional
    fun finishLeenk(
        userId: Long,
        leenkId: Long,
    ) {
        userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)

        if (leenk.author.id != userId) {
            throw NotLeenkOwnerException()
        }
        if (leenk.status == LeenkStatus.FINISHED) {
            throw LeenkAlreadyFinishedException()
        }

        leenk.changeStatusToFinished()
    }

    @Transactional
    fun kickParticipant(
        userId: Long,
        leenkId: Long,
        participantId: Long,
    ) {
        val leenk = leenkGetService.findById(leenkId)

        if (leenk.status != LeenkStatus.RECRUITING) {
            throw LeenkNotRecruitingException()
        }

        if (leenk.author.id != userId) {
            throw NotLeenkOwnerException()
        }

        if (userId == participantId) {
            throw CannotKickSelfException()
        }

        val participant = leenkParticipantsGetService.findByLeenkAndParticipantId(leenk.id!!, participantId)

        leenkParticipantsDeleteService.delete(participant)
        leenk.decreaseCurrentParticipants()

        leenkNotificationUsecase.saveKickedFromLeenkNotification(leenk, participant.participant)
    }

    @Transactional
    fun deleteLeenk(
        userId: Long,
        leenkId: Long,
    ) {
        userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)
        val participants = leenkParticipantsGetService.findAllByLeenk(leenk)

        if (leenk.author.id != userId) {
            throw NotLeenkOwnerException()
        }

        leenkParticipantsDeleteService.deleteAll(participants)
        mediaGetService.findFirstMediaByLeenk(leenk).ifPresent { mediaDeleteService.delete(it) }
        leenkDeleteService.delete(leenk)
    }

    @Transactional
    fun leaveLeenk(
        userId: Long,
        leenkId: Long,
    ) {
        val user = userGetService.findById(userId)
        val leenk = leenkGetService.findById(leenkId)

        if (leenk.author.id == userId) {
            throw CannotLeaveAsHostException()
        }

        if (leenk.status != LeenkStatus.RECRUITING) {
            throw LeenkNotRecruitingException()
        }

        val joined = leenkParticipantsGetService.existsByLeenkAndParticipant(leenk, user)
        if (!joined) {
            throw LeenkParticipantNotFoundException()
        }

        val participant = leenkParticipantsGetService.findByLeenkAndParticipantId(leenk.id!!, userId)
        leenkParticipantsDeleteService.delete(participant)

        leenk.decreaseCurrentParticipants()

        leenkNotificationUsecase.saveLeenkLeftNotification(leenk, user)
    }
}
