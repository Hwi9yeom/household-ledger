package com.aurfebre.household.service;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LedgerEntryService {

    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerEntryService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getAllEntries() {
        return ledgerEntryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getEntriesByUserId(Long userId) {
        return ledgerEntryRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getEntriesByUserIdAndType(Long userId, EntryType entryType) {
        return ledgerEntryRepository.findByUserIdAndEntryTypeOrderByDateDesc(userId, entryType);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getEntriesByUserIdAndCategory(Long userId, Long categoryId) {
        return ledgerEntryRepository.findByUserIdAndCategoryIdOrderByDateDesc(userId, categoryId);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getEntriesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return ledgerEntryRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getEntriesByUserIdAndTypeAndDateRange(
            Long userId, EntryType entryType, LocalDate startDate, LocalDate endDate) {
        return ledgerEntryRepository.findByUserIdAndEntryTypeAndDateBetweenOrderByDateDesc(
                userId, entryType, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByUserIdAndTypeAndDateRange(
            Long userId, EntryType entryType, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(
                userId, entryType, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByUserIdAndCategoryAndDateRange(
            Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = ledgerEntryRepository.sumByUserIdAndCategoryIdAndDateBetween(
                userId, categoryId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Optional<LedgerEntry> getEntryById(Long id) {
        return ledgerEntryRepository.findById(id);
    }

    public LedgerEntry createEntry(LedgerEntry entry) {
        if (entry.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        if (entry.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Entry date cannot be in the future");
        }
        
        return ledgerEntryRepository.save(entry);
    }

    public LedgerEntry updateEntry(Long id, LedgerEntry entryDetails) {
        LedgerEntry entry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LedgerEntry not found with id: " + id));

        if (entryDetails.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        if (entryDetails.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Entry date cannot be in the future");
        }

        entry.setEntryType(entryDetails.getEntryType());
        entry.setTransactionType(entryDetails.getTransactionType());
        entry.setCategoryId(entryDetails.getCategoryId());
        entry.setAmount(entryDetails.getAmount());
        entry.setDate(entryDetails.getDate());
        entry.setDescription(entryDetails.getDescription());
        entry.setMemo(entryDetails.getMemo());
        
        return ledgerEntryRepository.save(entry);
    }

    public void deleteEntry(Long id) {
        if (!ledgerEntryRepository.existsById(id)) {
            throw new IllegalArgumentException("LedgerEntry not found with id: " + id);
        }
        ledgerEntryRepository.deleteById(id);
    }
}