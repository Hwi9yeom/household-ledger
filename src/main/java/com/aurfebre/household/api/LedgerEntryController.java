package com.aurfebre.household.api;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.service.LedgerEntryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ledger-entries")
public class LedgerEntryController {

    private final LedgerEntryService ledgerEntryService;

    public LedgerEntryController(LedgerEntryService ledgerEntryService) {
        this.ledgerEntryService = ledgerEntryService;
    }

    @GetMapping
    public List<LedgerEntry> getAllEntries() {
        return ledgerEntryService.getAllEntries();
    }

    @GetMapping("/user/{userId}")
    public List<LedgerEntry> getEntriesByUserId(@PathVariable Long userId) {
        return ledgerEntryService.getEntriesByUserId(userId);
    }

    @GetMapping("/user/{userId}/type/{entryType}")
    public List<LedgerEntry> getEntriesByUserIdAndType(
            @PathVariable Long userId, 
            @PathVariable EntryType entryType) {
        return ledgerEntryService.getEntriesByUserIdAndType(userId, entryType);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public List<LedgerEntry> getEntriesByUserIdAndCategory(
            @PathVariable Long userId, 
            @PathVariable Long categoryId) {
        return ledgerEntryService.getEntriesByUserIdAndCategory(userId, categoryId);
    }

    @GetMapping("/user/{userId}/range")
    public List<LedgerEntry> getEntriesByUserIdAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ledgerEntryService.getEntriesByUserIdAndDateRange(userId, startDate, endDate);
    }

    @GetMapping("/user/{userId}/type/{entryType}/range")
    public List<LedgerEntry> getEntriesByUserIdAndTypeAndDateRange(
            @PathVariable Long userId,
            @PathVariable EntryType entryType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ledgerEntryService.getEntriesByUserIdAndTypeAndDateRange(userId, entryType, startDate, endDate);
    }

    @GetMapping("/user/{userId}/type/{entryType}/total")
    public BigDecimal getTotalByUserIdAndTypeAndDateRange(
            @PathVariable Long userId,
            @PathVariable EntryType entryType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ledgerEntryService.getTotalByUserIdAndTypeAndDateRange(userId, entryType, startDate, endDate);
    }

    @GetMapping("/user/{userId}/category/{categoryId}/total")
    public BigDecimal getTotalByUserIdAndCategoryAndDateRange(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ledgerEntryService.getTotalByUserIdAndCategoryAndDateRange(userId, categoryId, startDate, endDate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LedgerEntry> getEntryById(@PathVariable Long id) {
        return ledgerEntryService.getEntryById(id)
                .map(entry -> ResponseEntity.ok().body(entry))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LedgerEntry createEntry(@RequestBody LedgerEntry entry) {
        return ledgerEntryService.createEntry(entry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LedgerEntry> updateEntry(@PathVariable Long id, @RequestBody LedgerEntry entryDetails) {
        try {
            LedgerEntry updatedEntry = ledgerEntryService.updateEntry(id, entryDetails);
            return ResponseEntity.ok(updatedEntry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id) {
        try {
            ledgerEntryService.deleteEntry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}