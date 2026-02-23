package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.domain.entity.Location
import leets.leenk.domain.leenk.domain.repository.LocationRepository
import org.springframework.stereotype.Service

@Service
class LocationSaveService(
    private val locationRepository: LocationRepository,
) {
    fun save(location: Location): Location = locationRepository.save(location)
}
