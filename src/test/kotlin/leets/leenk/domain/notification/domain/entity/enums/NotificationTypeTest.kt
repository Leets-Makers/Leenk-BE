package leets.leenk.domain.notification.domain.entity.enums

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NotificationTypeTest :
    StringSpec({

        "{name}을 파라미터로 치환해야 한다" {
            val type = NotificationType.FEED_TAG
            val userName = "홍길동"

            val result = type.formatContent(name = userName)

            result shouldBe "[홍길동]이 나를 함께한 사람에 추가했어"
        }

        "{title}을 파라미터로 치환해야 한다" {
            val type = NotificationType.LEENK_JOIN_COMPLETED
            val leenkTitle = "스터디 모임"

            val result = type.formatContent(title = leenkTitle)

            result shouldBe "[스터디 모임]에 참여했어"
        }

        "정수형 포맷 문자열(%d)을 올바르게 치환해야 한다" {
            val type = NotificationType.FEED_REACTION_COUNT
            val reactionCount = 25

            val result = type.formatContent(count = reactionCount)

            result shouldBe "내가 쓴 피드에 좋아요를 25개 받았어"
        }

        "파라미터가 없는 경우 원본 content를 그대로 반환해야 한다" {
            val type = NotificationType.NEW_LEENK

            val result = type.formatContent()

            result shouldBe "새로운 모임을 확인해 봐"
        }

        "모든 {name}을 같은 파라미터로 치환해야 한다" {
            val type = NotificationType.BIRTHDAY_CELEBRATE
            val userName = "김철수"

            val result = type.formatContent(name = userName)

            result shouldBe "생일 축하해, 멋쟁이 [김철수]!"
        }

        "잘못된 파라미터가 제공된 경우 원본 content를 반환해야 한다" {
            val type = NotificationType.FEED_REACTION_COUNT

            val result = type.formatContent()

            result shouldBe type.content
        }
    })
