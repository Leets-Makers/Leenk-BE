package leets.leenk.domain.leenk.domain.service;

import leets.leenk.domain.leenk.domain.entity.Location;
import leets.leenk.domain.leenk.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationSaveService {

    private final LocationRepository locationRepository;

    public Location save(Location location) {
        return locationRepository.save(location);
    }
}
