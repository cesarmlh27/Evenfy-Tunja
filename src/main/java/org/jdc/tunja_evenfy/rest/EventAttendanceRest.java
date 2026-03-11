package org.jdc.tunja_evenfy.rest;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.EventAttendanceDTO;
import org.jdc.tunja_evenfy.service.EventAttendanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/even-attendance")
@RequiredArgsConstructor
public class EventAttendanceRest {

    private final EventAttendanceService attendanceService;

    @GetMapping
    public List<EventAttendanceDTO> findAll() {
        return attendanceService.findAll();
    }

    @GetMapping("/{id}")
    public EventAttendanceDTO findById(@PathVariable UUID id) {
        return attendanceService.findById(id);
    }

    @PostMapping
    public EventAttendanceDTO create(@RequestBody EventAttendanceDTO dto) {
        return attendanceService.create(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        attendanceService.delete(id);
    }
}
