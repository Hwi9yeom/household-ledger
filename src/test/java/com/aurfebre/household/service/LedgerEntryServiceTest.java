package com.aurfebre.household.service;

import com.aurfebre.household.domain.LedgerEntry;
import com.aurfebre.household.domain.enums.EntryType;
import com.aurfebre.household.domain.enums.TransactionType;
import com.aurfebre.household.repository.LedgerEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerEntryServiceTest {

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private LedgerEntryService ledgerEntryService;

    private LedgerEntry testEntry;

    @BeforeEach
    void setUp() {
        testEntry = new LedgerEntry(
            1L, 
            EntryType.INCOME, 
            TransactionType.FIXED, 
            1L, 
            new BigDecimal("3000000"), 
            LocalDate.now(), 
            "월급"
        );
        testEntry.setId(1L);
        testEntry.setMemo("정기 급여");
        testEntry.setCreatedAt(LocalDateTime.now());
        testEntry.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllEntries_ShouldReturnAllEntries() {
        // Given
        List<LedgerEntry> entries = Arrays.asList(
            testEntry,
            new LedgerEntry(1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L, 
                           new BigDecimal("15000"), LocalDate.now(), "점심")
        );
        when(ledgerEntryRepository.findAll()).thenReturn(entries);

        // When
        List<LedgerEntry> result = ledgerEntryService.getAllEntries();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testEntry);
        verify(ledgerEntryRepository).findAll();
    }

    @Test
    void getEntriesByUserId_ShouldReturnUserEntries() {
        // Given
        List<LedgerEntry> entries = Arrays.asList(testEntry);
        when(ledgerEntryRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(entries);

        // When
        List<LedgerEntry> result = ledgerEntryService.getEntriesByUserId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(testEntry);
        verify(ledgerEntryRepository).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    void getEntriesByUserIdAndType_ShouldReturnFilteredEntries() {
        // Given
        List<LedgerEntry> entries = Arrays.asList(testEntry);
        when(ledgerEntryRepository.findByUserIdAndEntryTypeOrderByDateDesc(1L, EntryType.INCOME))
            .thenReturn(entries);

        // When
        List<LedgerEntry> result = ledgerEntryService.getEntriesByUserIdAndType(1L, EntryType.INCOME);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntryType()).isEqualTo(EntryType.INCOME);
        verify(ledgerEntryRepository).findByUserIdAndEntryTypeOrderByDateDesc(1L, EntryType.INCOME);
    }

    @Test
    void getEntriesByUserIdAndCategory_ShouldReturnCategoryEntries() {
        // Given
        List<LedgerEntry> entries = Arrays.asList(testEntry);
        when(ledgerEntryRepository.findByUserIdAndCategoryIdOrderByDateDesc(1L, 1L))
            .thenReturn(entries);

        // When
        List<LedgerEntry> result = ledgerEntryService.getEntriesByUserIdAndCategory(1L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
        verify(ledgerEntryRepository).findByUserIdAndCategoryIdOrderByDateDesc(1L, 1L);
    }

    @Test
    void getEntriesByUserIdAndDateRange_ShouldReturnEntriesInRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<LedgerEntry> entries = Arrays.asList(testEntry);
        when(ledgerEntryRepository.findByUserIdAndDateBetweenOrderByDateDesc(1L, startDate, endDate))
            .thenReturn(entries);

        // When
        List<LedgerEntry> result = ledgerEntryService.getEntriesByUserIdAndDateRange(1L, startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        verify(ledgerEntryRepository).findByUserIdAndDateBetweenOrderByDateDesc(1L, startDate, endDate);
    }

    @Test
    void getTotalByUserIdAndTypeAndDateRange_ShouldReturnTotal() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        BigDecimal expectedTotal = new BigDecimal("3000000");
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(1L, EntryType.INCOME, startDate, endDate))
            .thenReturn(expectedTotal);

        // When
        BigDecimal result = ledgerEntryService.getTotalByUserIdAndTypeAndDateRange(1L, EntryType.INCOME, startDate, endDate);

        // Then
        assertThat(result).isEqualTo(expectedTotal);
        verify(ledgerEntryRepository).sumByUserIdAndEntryTypeAndDateBetween(1L, EntryType.INCOME, startDate, endDate);
    }

    @Test
    void getTotalByUserIdAndTypeAndDateRange_WhenNoData_ShouldReturnZero() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(ledgerEntryRepository.sumByUserIdAndEntryTypeAndDateBetween(1L, EntryType.INCOME, startDate, endDate))
            .thenReturn(null);

        // When
        BigDecimal result = ledgerEntryService.getTotalByUserIdAndTypeAndDateRange(1L, EntryType.INCOME, startDate, endDate);

        // Then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
        verify(ledgerEntryRepository).sumByUserIdAndEntryTypeAndDateBetween(1L, EntryType.INCOME, startDate, endDate);
    }

    @Test
    void getEntryById_WhenEntryExists_ShouldReturnEntry() {
        // Given
        when(ledgerEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // When
        Optional<LedgerEntry> result = ledgerEntryService.getEntryById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testEntry);
        verify(ledgerEntryRepository).findById(1L);
    }

    @Test
    void createEntry_WhenValidEntry_ShouldCreateEntry() {
        // Given
        LedgerEntry newEntry = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("15000"), LocalDate.now(), "점심"
        );
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenReturn(newEntry);

        // When
        LedgerEntry result = ledgerEntryService.createEntry(newEntry);

        // Then
        assertThat(result).isEqualTo(newEntry);
        verify(ledgerEntryRepository).save(newEntry);
    }

    @Test
    void createEntry_WhenAmountIsZero_ShouldThrowException() {
        // Given
        LedgerEntry invalidEntry = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            BigDecimal.ZERO, LocalDate.now(), "점심"
        );

        // When & Then
        assertThatThrownBy(() -> ledgerEntryService.createEntry(invalidEntry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than zero");
        
        verify(ledgerEntryRepository, never()).save(any(LedgerEntry.class));
    }

    @Test
    void createEntry_WhenAmountIsNegative_ShouldThrowException() {
        // Given
        LedgerEntry invalidEntry = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("-100"), LocalDate.now(), "점심"
        );

        // When & Then
        assertThatThrownBy(() -> ledgerEntryService.createEntry(invalidEntry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than zero");
        
        verify(ledgerEntryRepository, never()).save(any(LedgerEntry.class));
    }

    @Test
    void createEntry_WhenDateIsInFuture_ShouldThrowException() {
        // Given
        LedgerEntry invalidEntry = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("15000"), LocalDate.now().plusDays(1), "점심"
        );

        // When & Then
        assertThatThrownBy(() -> ledgerEntryService.createEntry(invalidEntry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Entry date cannot be in the future");
        
        verify(ledgerEntryRepository, never()).save(any(LedgerEntry.class));
    }

    @Test
    void updateEntry_WhenValidUpdate_ShouldUpdateEntry() {
        // Given
        LedgerEntry updateDetails = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("20000"), LocalDate.now(), "수정된 점심"
        );
        updateDetails.setMemo("수정된 메모");
        
        when(ledgerEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenReturn(testEntry);

        // When
        LedgerEntry result = ledgerEntryService.updateEntry(1L, updateDetails);

        // Then
        assertThat(result.getEntryType()).isEqualTo(EntryType.EXPENSE);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("20000"));
        assertThat(result.getDescription()).isEqualTo("수정된 점심");
        verify(ledgerEntryRepository).findById(1L);
        verify(ledgerEntryRepository).save(testEntry);
    }

    @Test
    void updateEntry_WhenEntryNotExists_ShouldThrowException() {
        // Given
        LedgerEntry updateDetails = new LedgerEntry(
            1L, EntryType.EXPENSE, TransactionType.VARIABLE, 2L,
            new BigDecimal("20000"), LocalDate.now(), "수정된 점심"
        );
        when(ledgerEntryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ledgerEntryService.updateEntry(1L, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LedgerEntry not found with id: 1");
        
        verify(ledgerEntryRepository).findById(1L);
        verify(ledgerEntryRepository, never()).save(any(LedgerEntry.class));
    }

    @Test
    void deleteEntry_WhenEntryExists_ShouldDeleteEntry() {
        // Given
        when(ledgerEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // When
        ledgerEntryService.deleteEntry(1L);

        // Then
        verify(ledgerEntryRepository).findById(1L);
        verify(ledgerEntryRepository).deleteById(1L);
    }

    @Test
    void deleteEntry_WhenEntryNotExists_ShouldThrowException() {
        // Given
        when(ledgerEntryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ledgerEntryService.deleteEntry(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LedgerEntry not found with id: 1");
        
        verify(ledgerEntryRepository).findById(1L);
        verify(ledgerEntryRepository, never()).deleteById(any());
    }
}