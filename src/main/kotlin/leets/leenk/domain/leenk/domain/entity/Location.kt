package leets.leenk.domain.leenk.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import leets.leenk.global.common.entity.BaseEntity

@Entity
@Table(name = "locations")
class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, length = 25)
    var placeName: String,
) : BaseEntity() {
    fun updatePlaceName(placeName: String) {
        this.placeName = placeName
    }
}
