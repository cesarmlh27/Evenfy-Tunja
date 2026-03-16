package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.dto.LocationDTO;
import org.jdc.tunja_evenfy.entity.LocationEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.LocationRepository;
import org.jdc.tunja_evenfy.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImplement implements LocationService {

    private final LocationRepository locationRepository;

    private LocationDTO toDTO(LocationEntity e) {
        return LocationDTO.builder()
                .id(e.getId())
                .placeName(e.getPlaceName())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .build();
    }

    private LocationEntity toEntity(LocationDTO dto) {
        return LocationEntity.builder()
                .id(dto.getId())
                .placeName(dto.getPlaceName() != null ? dto.getPlaceName().trim() : null)
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    @Override
    public List<LocationDTO> findAll() {
        return locationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public LocationDTO findById(UUID id) {
        LocationEntity e = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));
        return toDTO(e);
    }

    @Override
    public LocationDTO create(LocationDTO dto) {
        if (dto == null || dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty()) {
            throw new BadRequestException("placeName is required");
        }
        return toDTO(locationRepository.save(toEntity(dto)));
    }

    @Override
    public LocationDTO update(UUID id, LocationDTO dto) {
        if (dto == null || dto.getPlaceName() == null || dto.getPlaceName().trim().isEmpty()) {
            throw new BadRequestException("placeName is required");
        }

        LocationEntity e = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));

        e.setPlaceName(dto.getPlaceName().trim());
        e.setAddress(dto.getAddress());
        e.setLatitude(dto.getLatitude());
        e.setLongitude(dto.getLongitude());

        return toDTO(locationRepository.save(e));
    }

    @Override
    public void delete(UUID id) {
        LocationEntity e = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));
        locationRepository.delete(e);
    }
}
