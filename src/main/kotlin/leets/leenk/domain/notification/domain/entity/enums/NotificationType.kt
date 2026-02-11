package leets.leenk.domain.notification.domain.entity.enums

enum class NotificationType(
    val title: String,
    val content: String,
    val path: String,
) {
    // Leenk
    NEW_LEENK(
        title = "Leenk",
        content = "새로운 모임을 확인해 봐",
        path = "/leenks",
    ),
    LEENK_JOIN_COMPLETED(
        title = "Leenk",
        content = "[{title}]에 참여했어",
        path = "/leenks",
    ),
    NEW_LEENK_PARTICIPANT(
        title = "Leenk",
        content = "[{title}]에 새로운 참여자가 들어왔어",
        path = "/leenks",
    ),
    KICKED_FROM_LEENK(
        title = "Leenk",
        content = "모임에서 내보내졌어",
        path = "/leenks",
    ),
    LEENK_CLOSED(
        title = "Leenk",
        content = "[{title}]의 모집이 종료됐어\n모임원들을 확인해 봐!",
        path = "/leenks",
    ),
    LEENK_STARTING_SOON(
        title = "Leenk",
        content = "[{title}] 시작 30분 전이야",
        path = "/leenks",
    ),
    LEENK_FINISHED(
        title = "Leenk",
        content = "모임이 끝났어. 후기 쓰러 가볼까?",
        path = "/leenks",
    ),
    LEENK_STARTED_HOST_REMINDER(
        title = "Leenk",
        content = "모임이 시작됐어! 모집을 종료할까?",
        path = "/leenks",
    ),
    LEENK_LEFT(
        title = "Leenk",
        content = "[{name}]이 모임에서 나갔어",
        path = "/leenks",
    ),

    // Feed
    FEED_TAG(
        title = "Leenk",
        content = "[{name}]이 나를 함께한 사람에 추가했어",
        path = "/feeds",
    ),
    NEW_FEED(
        title = "Leenk",
        content = "새로운 게시글을 확인해 봐",
        path = "/feeds",
    ),
    FEED_FIRST_REACTION(
        title = "Leenk",
        content = "내가 쓴 피드에 좋아요를 받았어\n[{name}]",
        path = "/feeds",
    ),
    FEED_REACTION_COUNT(
        title = "Leenk",
        content = "내가 쓴 피드에 좋아요를 %d개 받았어",
        path = "/feeds",
    ),

    // Birthday
    BIRTHDAY_ANNOUNCEMENT(
        title = "Leenk",
        content = "오늘은 [{name}]의 생일이야\n축하해주러 가볼까?",
        path = "/birthday/users",
    ),
    BIRTHDAY_CELEBRATE(
        title = "Leenk",
        content = "생일 축하해, 멋쟁이 [{name}]!",
        path = "/birthday/users",
    ),
    BIRTHDAY_LETTER(
        title = "Leenk",
        content = "[{name}]에게 생일 편지를 받았어!",
        path = "/birthday/letters/me",
    ),
    ;

    fun formatContent(
        name: String? = null,
        title: String? = null,
        count: Any? = null,
    ): String {
        var formatted =
            content
                .replace("{name}", name ?: "{name}")
                .replace("{title}", title ?: "{title}")
                .replace("{count}", count?.toString() ?: "{count}")

        return try {
            if (formatted.contains("%d") && count != null) {
                String.format(formatted, count)
            } else {
                formatted
            }
        } catch (e: Exception) {
            content
        }
    }
}
