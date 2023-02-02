package com.nextstep.interfaces.theme;

import com.nextstep.interfaces.theme.dtos.ThemeResponse;
import lombok.RequiredArgsConstructor;
import com.nextstep.domains.theme.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/themes")
@RestController
public class ThemeController {

    private final ThemeService service;


    @GetMapping("/{theme_id}")
    public ResponseEntity<ThemeResponse> getById(@PathVariable("theme_id") Long themeId) {

        return ResponseEntity.ok(service.getById(themeId));
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}