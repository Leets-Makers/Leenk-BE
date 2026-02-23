package leets.leenk.domain.leenk.test.fixture

import leets.leenk.domain.leenk.domain.entity.Location

class LocationTestFixture {
    companion object {
        fun createLocation(
            id: Long? = null,
            placeName: String = "테스트 장소",
        ): Location =
            Location(
                id = id,
                placeName = placeName,
            )
    }
}
