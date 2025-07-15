package leets.leenk.domain.notification.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    FEED_TAG("피드 태그", "%s이 나를 함께한 사람에 추가했어"),
    NEW_FEED("새로운 피드", "새로운 게시글을 확인해 봐"),
    FEED_FIRST_REACTION("새로운 공감", "내가 쓴 피드에 좋아요를 받았어"),
    FEED_REACTION_COUNT("누적 공감", "내가 쓴 피드에 좋아요를 %d개 받았어");


    private final String title;
    private final String content;

    public String getFormattedContent(String name) {
        return String.format(content, name);
    }

    public String getFormattedContent(Long reactionCount) {
        return String.format(content, reactionCount);
    }
}

