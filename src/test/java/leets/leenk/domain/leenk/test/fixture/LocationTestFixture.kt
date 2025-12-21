package leets.leenk.domain.leenk.test.fixture

import leets.leenk.domain.leenk.domain.entity.Location

class LocationTestFixture {
    companion object {
        fun createLocation(
            id: Long? = null,
            placeName: String = "테스트 장소"
        ): Location {
            return Location.builder()
                .apply { id?.let { id(it) } }
                .placeName(placeName)
                .build()
        }
    }
}