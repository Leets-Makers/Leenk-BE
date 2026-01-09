package leets.leenk.domain.notification.domain.entity;

import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.global.common.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "notifications")
public class Notification extends MongoBaseEntity {

    @Id
    private String id;

    private Long userId;

    private NotificationType notificationType;

    private Boolean isRead;

    private NotificationContent content;

    public void markUnread() {
        this.isRead = false;
    }

    public void markRead() {
        this.isRead = true;
    }

}
