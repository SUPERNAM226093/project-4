package com.myproject.clinic.hospital.controller;

import com.myproject.clinic.hospital.dto.HospitalResponse;
import com.myproject.clinic.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// GET  http://localhost:8080/api/hospitals
// GET  http://localhost:8080/api/hospitals/{slug}
@Slf4j
@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<HospitalResponse>> getAll() {
        log.info("REST request to get all hospitals");
        return ResponseEntity.ok(hospitalService.findAll());
    }

    /**
     * Phương thức: Lấy theo đường dẫn rút gọn (slug).
     */
    @GetMapping("/{slug}")
    public ResponseEntity<HospitalResponse> getBySlug(@PathVariable String slug) {
        log.info("REST request to get hospital by slug : {}", slug);
        return ResponseEntity.ok(hospitalService.findBySlug(slug));
    }
}
