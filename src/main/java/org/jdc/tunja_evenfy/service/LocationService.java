package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.LocationDTO;

import java.util.List;
import java.util.UUID;

public interface LocationService {
    List<LocationDTO> findAll();
    LocationDTO findById(UUID id);
    LocationDTO create(LocationDTO dto);
    LocationDTO update(UUID id, LocationDTO dto);
    void delete(UUID id);
}
