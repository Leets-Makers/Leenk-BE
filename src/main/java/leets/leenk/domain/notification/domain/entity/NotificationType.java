package leets.leenk.domain.notification.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // Leenk
    NEW_LEENK("Leenk", "새로운 모임을 확인해 봐."),
    LEENK_JOIN_COMPLETED("Leenk", "에 참여했어"),
    NEW_LEENK_PARTICIPANT("Leenk", "에 새로운 참여자가 들어왔어"),

    // Feed
    FEED_TAG("Leenk", "%s이 나를 함께한 사람에 추가했어"),
    NEW_FEED("Leenk", "새로운 게시글을 확인해 봐"),
    FEED_FIRST_REACTION("Leenk", "내가 쓴 피드에 좋아요를 받았어"),
    FEED_REACTION_COUNT("Leenk", "내가 쓴 피드에 좋아요를 %d개 받았어");


    private final String title;
    private final String content;

    public String getFormattedContent(String name) {
        return String.format(content, name);
    }

    public String getFormattedContent(Long reactionCount) {
        return String.format(content, reactionCount);
    }
}
