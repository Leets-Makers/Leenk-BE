package leets.leenk.domain.leenk.application.mapper;

import leets.leenk.domain.leenk.domain.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toLocation(String placeName) {
        return Location.builder()
                .placeName(placeName)
                .build();
    }
}
