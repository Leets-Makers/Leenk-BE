package leets.leenk.domain.user.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name="user_backup_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBackupInfo {
    @Id
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String name;

    private String profileImage;

    private String thumbnail;
}
