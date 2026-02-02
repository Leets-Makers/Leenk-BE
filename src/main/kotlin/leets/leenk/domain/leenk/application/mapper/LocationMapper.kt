package leets.leenk.domain.leenk.application.mapper

import leets.leenk.domain.leenk.domain.entity.Location
import org.springframework.stereotype.Component

@Component
class LocationMapper {
    fun toLocation(placeName: String): Location = Location(placeName = placeName)
}
