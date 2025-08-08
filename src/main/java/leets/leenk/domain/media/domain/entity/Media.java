package leets.leenk.domain.media.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.media.domain.entity.enums.MediaType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(
        name = "medias",
        indexes = {
                @Index(name = "idx_feed_position", columnList = "feed_id, position"),
                @Index(name = "idx_leenk_position", columnList = "leenk_id, position")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_feed_position", columnNames = {"feed_id", "position"}),
                @UniqueConstraint(name = "uk_leenk_position", columnNames = {"leenk_id", "position"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mediaUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(nullable = false)
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", updatable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leenk_id", updatable = false)
    private Leenk leenk;
}
