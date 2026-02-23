package leets.leenk.domain.leenk.domain.repository

import leets.leenk.domain.leenk.domain.entity.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Long>
