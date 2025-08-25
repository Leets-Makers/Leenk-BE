package leets.leenk.domain.notification.domain.entity.leenkContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@SuperBuilder
public class NewLeenkParticipantDetail extends NotificationContent {

    private Long participantId;

    private String participantName;

}
