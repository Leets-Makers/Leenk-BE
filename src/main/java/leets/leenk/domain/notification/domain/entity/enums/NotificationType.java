package leets.leenk.domain.notification.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // Leenk
    NEW_LEENK("Leenk", "새로운 모임을 확인해 봐", "leenks"),
    LEENK_JOIN_COMPLETED("Leenk", "에 참여했어", "leenks"),
    NEW_LEENK_PARTICIPANT("Leenk", "에 새로운 참여자가 들어왔어", "leenks"),
    KICKED_FROM_LEENK("Leenk", "모임에서 내보내졌어", "leenks"),
    LEENK_CLOSED("Leenk", "의 모집이 종료됐어\n모임원들을 확인해 봐!", "leenks"),
    LEENK_STARTING_SOON("Leenk", " 시작 30분 전이야", "leenks"),
    LEENK_FINISHED("Leenk", "모임이 끝났어. 후기 쓰러 가볼까?", "leenks"),
    LEENK_STARTED_HOST_REMINDER("Leenk", "모임이 시작됐어! 모집을 종료할까?", "leenks"),
    LEENK_LEFT("Leenk", "이 모임에서 나갔어", "leenks"),

    // Feed
    FEED_TAG("Leenk", "이 나를 함께한 사람에 추가했어", "feeds"),
    NEW_FEED("Leenk", "새로운 게시글을 확인해 봐", "feeds"),
    FEED_FIRST_REACTION("Leenk", "내가 쓴 피드에 좋아요를 받았어", "feeds"),
    FEED_REACTION_COUNT("Leenk", "내가 쓴 피드에 좋아요를 %d개 받았어", "feeds"),

    // Birthday
    BIRTHDAY_ANNOUNCEMENT("Leenk", "오늘은 {name}의 생일이야\n축하해주러 가볼까?", "birthday/users"),
    BIRTHDAY_CELEBRATE("Leenk", "생일 축하해, 멋쟁이 {name}!", "birthday/users"),
    BIRTHDAY_LETTER("Leenk", "{name}에게 생일 편지를 받았어!", "birthday/letters/me");

    private final String title;
    private final String content;
    private final String path;

    public String getFormattedContent(Long reactionCount) {
        return String.format(content, reactionCount);
    }
}
