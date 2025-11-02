package leets.leenk.domain.notification.domain.entity.birthdayContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class BirthdayCelebrateContent extends NotificationContent {
    private String birthdayUserName;
}
