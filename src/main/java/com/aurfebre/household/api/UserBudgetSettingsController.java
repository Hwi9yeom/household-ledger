package com.aurfebre.household.api;

import com.aurfebre.household.domain.UserBudgetSettings;
import com.aurfebre.household.dto.UserBudgetSettingsDto;
import com.aurfebre.household.mapper.UserBudgetSettingsMapper;
import com.aurfebre.household.service.UserBudgetSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user-settings")
public class UserBudgetSettingsController {

    private final UserBudgetSettingsService service;

    public UserBudgetSettingsController(UserBudgetSettingsService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserBudgetSettingsDto> getSettings(@PathVariable Long userId) {
        return service.getSettingsByUserId(userId)
                .map(UserBudgetSettingsMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserBudgetSettingsDto> createSettings(@RequestBody UserBudgetSettingsDto dto) {
        try {
            UserBudgetSettings created = service.createSettings(UserBudgetSettingsMapper.toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(UserBudgetSettingsMapper.toDto(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserBudgetSettingsDto> updateSettings(@PathVariable Long userId,
                                                                 @RequestBody UserBudgetSettingsDto dto) {
        try {
            UserBudgetSettings updated = service.updateSettings(userId, UserBudgetSettingsMapper.toEntity(dto));
            return ResponseEntity.ok(UserBudgetSettingsMapper.toDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteSettings(@PathVariable Long userId) {
        try {
            service.deleteSettings(userId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
