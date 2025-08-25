package leets.leenk.domain.notification.domain.entity.leenkContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
public class NewLeenkParticipantNotificationContent extends NotificationContent {

    private Long leenkId;

    private String leenkTitle;

    @Builder.Default
    private List<NewLeenkParticipantDetail> newLeenkParticipantDetails = new ArrayList<>();

}
