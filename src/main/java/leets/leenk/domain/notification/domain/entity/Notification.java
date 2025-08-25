package leets.leenk.domain.notification.domain.entity;

import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import leets.leenk.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "notifications")
public class Notification extends BaseEntity {

    @Id
    private String id;

    private Long userId;

    @Enumerated(EnumType.STRING)
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
