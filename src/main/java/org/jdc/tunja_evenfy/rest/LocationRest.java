package org.jdc.tunja_evenfy.rest;


import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.LocationDTO;
import org.jdc.tunja_evenfy.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/locations")
@RequiredArgsConstructor
public class LocationRest {

    private final LocationService locationService;

    @GetMapping
    public List<LocationDTO> findAll() {
        return locationService.findAll();
    }

    @GetMapping("/{id}")
    public LocationDTO findById(@PathVariable UUID id) {
        return locationService.findById(id);
    }

    @PostMapping
    public LocationDTO create(@RequestBody LocationDTO dto) {
        return locationService.create(dto);
    }

    @PutMapping("/{id}")
    public LocationDTO update(@PathVariable UUID id, @RequestBody LocationDTO dto) {
        return locationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        locationService.delete(id);
    }
}
