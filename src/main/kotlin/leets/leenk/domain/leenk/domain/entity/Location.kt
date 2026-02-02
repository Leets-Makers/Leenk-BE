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

    companion object {
        @JvmStatic
        fun builder(): LocationBuilder = LocationBuilder()
    }

    class LocationBuilder {
        private var id: Long? = null
        private var placeName: String = ""

        fun id(id: Long?): LocationBuilder {
            this.id = id
            return this
        }

        fun placeName(placeName: String): LocationBuilder {
            this.placeName = placeName
            return this
        }

        fun build(): Location = Location(id = id, placeName = placeName)
    }
}
