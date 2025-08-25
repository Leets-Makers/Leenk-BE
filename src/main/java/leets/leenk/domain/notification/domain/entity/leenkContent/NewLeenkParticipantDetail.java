package leets.leenk.domain.notification.domain.entity.leenkContent;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NewLeenkParticipantDetail {

    private Long participantId;

    private String participantName;

}
