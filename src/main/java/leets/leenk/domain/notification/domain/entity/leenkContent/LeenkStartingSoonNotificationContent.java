package leets.leenk.domain.notification.domain.entity.leenkContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@Getter
public class LeenkStartingSoonNotificationContent extends NotificationContent {

    private Long leenkId;

    private String leenkTitle;

    private Long placeId;

    private String placeName;

    private LocalDateTime startTime;

}
