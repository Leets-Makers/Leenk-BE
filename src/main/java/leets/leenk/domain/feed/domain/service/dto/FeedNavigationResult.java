package leets.leenk.domain.feed.domain.service.dto;

import leets.leenk.domain.feed.domain.entity.Feed;

import java.util.List;

/**
 * 피드 네비게이션 조회 결과
 * 조회된 피드 목록과 추가 피드 존재 여부를 함께 반환
 */
public record FeedNavigationResult(
        List<Feed> feeds,
        boolean hasMore
) {
}
